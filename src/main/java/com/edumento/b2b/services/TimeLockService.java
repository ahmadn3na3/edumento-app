package com.edumento.b2b.services;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.TimeLock;
import com.edumento.b2b.domain.TimeLockException;
import com.edumento.b2b.mappers.OrganizationMapper;
import com.edumento.b2b.model.timelock.DayModel;
import com.edumento.b2b.model.timelock.TimeLockCreateModel;
import com.edumento.b2b.model.timelock.TimeLockExceptionCreationModel;
import com.edumento.b2b.model.timelock.TimeLockExceptionModel;
import com.edumento.b2b.model.timelock.TimeLockModel;
import com.edumento.b2b.model.timelock.TimeModel;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.b2b.repo.TimeLockExceptionRepository;
import com.edumento.b2b.repo.TimeLockRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.WeekDay;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;

@Service
public class TimeLockService {
	private final Logger log = LoggerFactory.getLogger(TimeLockService.class);
	private final TimeLockRepository timeLockRepository;
	private final TimeLockExceptionRepository timeLockExceptionRepository;
	private final OrganizationRepository organizationRepository;
	private final UserRepository userRepository;

	@Value("${mint.lockPass}")
	private String lockPass;

	@Autowired
	public TimeLockService(TimeLockRepository timeLockRepository,
			TimeLockExceptionRepository timeLockExceptionRepository, UserRepository userRepository,
			OrganizationRepository organizationRepository) {
		this.timeLockRepository = timeLockRepository;
		this.timeLockExceptionRepository = timeLockExceptionRepository;
		this.userRepository = userRepository;
		this.organizationRepository = organizationRepository;
	}

	private static boolean checkUserForOrganization(User user, Organization organization) {
		if (user.getType() == UserType.USER) {
			return false;
		}

		if (user.getType() == UserType.ADMIN || user.getType() == UserType.FOUNDATION_ADMIN) {
			return (user.getType() != UserType.FOUNDATION_ADMIN
					|| user.getFoundation().getId().equals(organization.getFoundation().getId()))
					&& (user.getType() != UserType.ADMIN || user.getOrganization().equals(organization));
		}
		return true;
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_CREATE)
	@PreAuthorize("hasAuthority('TIMELOCK_CREATE') AND hasAuthority('ADMIN')")
	public ResponseModel create(TimeLockCreateModel timeLockCreateModel) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				log.debug("create time lock {}", timeLockCreateModel);
				var organization = organizationRepository.findById(timeLockCreateModel.getOrganizationId())
						.orElseThrow(NotFoundException::new);

				if (!checkUserForOrganization(user, organization)) {
					log.warn("user {} not permitted", user.getId());
					throw new NotPermittedException();
				}
				if (timeLockRepository
						.findOneByNameAndOrganizationAndDeletedFalse(timeLockCreateModel.getName(), organization)
						.isPresent()) {
					log.warn("time lock {} already exist", timeLockCreateModel.getName());
					throw new ExistException("name");
				}
				var from = DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getFromDate());
				var to = DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getToDate());

				if (from.equals(to) || from.after(to)) {
					throw new MintException(Code.INVALID, "error.timelock.daterange");
				}
				var lock = new TimeLock();
				lock.setName(timeLockCreateModel.getName());
				lock.setFromDate(from);
				lock.setToDate(to);
				timeLockCreateModel.getDayModels().forEach(new Consumer<DayModel>() {
					@Override
					public void accept(DayModel model) {
						lock.getDays().put(model.getDay(), String.join(",",
								model.getTimeModels().stream().map(TimeModel::toString).collect(Collectors.toList())));
					}
				});

				lock.setOrganization(organization);
				lock.setFoundation(organization.getFoundation());
				lock.setUnlockPassword(timeLockCreateModel.getUnlockPassword());

				timeLockRepository.save(lock);
				extractUsers(timeLockCreateModel, organization, lock);
				timeLockRepository.save(lock);
				log.debug("time lock created");

				// timeLockCreateModel
				return ResponseModel.done(lock.getId());
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('TIMELOCK_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getAll(PageRequest page, Long organizationId) {
		log.debug("get all time locks");
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, PageResponseModel>() {
			@Override
			public PageResponseModel apply(User user) {
				Page<TimeLockModel> timeLockModels = null;
				Organization organization = null;

				if (organizationId != null) {
					organization = organizationRepository.findById(organizationId).orElse(null);
				}
				switch (user.getType()) {
				case SUPER_ADMIN:
				case SYSTEM_ADMIN:
					if (organization != null) {
						timeLockModels = timeLockRepository.findByOrganizationAndDeletedFalse(organization, page)
								.map(TimeLockService.this::mapTimeLock);
					} else {
						timeLockModels = timeLockRepository.findAll(page).map(TimeLockService.this::mapTimeLock);
					}
					break;
				case FOUNDATION_ADMIN:
					timeLockModels = organization != null
							? timeLockRepository.findByOrganizationAndDeletedFalse(organization, page)
									.map(TimeLockService.this::mapTimeLock)
							: timeLockRepository.findByFoundationAndDeletedFalse(user.getFoundation(), page)
									.map(TimeLockService.this::mapTimeLock);
					break;
				case ADMIN:
					if (!user.getOrganization().equals(organization)) {
						organization = user.getOrganization();
					}
					timeLockModels = timeLockRepository.findByOrganizationAndDeletedFalse(organization, page)
							.map(TimeLockService.this::mapTimeLock);
					break;
				default:
					throw new NotPermittedException();
				}
				return PageResponseModel.done(timeLockModels.getContent(), timeLockModels.getTotalPages(),
						timeLockModels.getNumber(), timeLockModels.getTotalElements());
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('TIMELOCK_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getById(Long id) {
		log.debug("get time lock {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var timeLock = timeLockRepository.findById(id).orElseThrow(NotFoundException::new);

				if (!checkUserForOrganization(user, timeLock.getOrganization())) {
					log.warn("user {} not permitted", user.getId());
					throw new NotPermittedException();
				}

				var timeLockModel = mapTimeLock(timeLock);
				timeLockModel.getTimeLockExceptionModels().addAll(timeLock.getTimeLockExceptions().stream()
						.filter(new Predicate<TimeLockException>() {
							@Override
							public boolean test(TimeLockException e) {
								return !e.isDeleted();
							}
						}).map(TimeLockService.this::mapTimeLockException).collect(Collectors.toSet()));
				return ResponseModel.done(timeLockModel);
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_DELETE)
	@PreAuthorize("hasAuthority('TIMELOCK_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel delete(Long id) {
		log.debug("delete time lock {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var timeLock = timeLockRepository.findById(id).orElseThrow(NotFoundException::new);

				if (!checkUserForOrganization(user, timeLock.getOrganization())) {
					log.warn("user {} not permitted", user.getId());
					throw new NotPermittedException();
				}
				if (!timeLock.getTimeLockExceptions().isEmpty()) {
					timeLockExceptionRepository.deleteAll(timeLock.getTimeLockExceptions());
				}
				userRepository.saveAll(timeLock.getUsers().stream().map(new Function<User, User>() {
					@Override
					public User apply(User user1) {
						user1.setTimeLock(null);
						return user1;
					}
				}).toList());
				timeLockRepository.delete(timeLock);
				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_UPDATE)
	@PreAuthorize("hasAuthority('TIMELOCK_UPDATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.TIME_LOCK_UPDATE, services = Services.NOTIFICATIONS)
	public ResponseModel update(Long id, TimeLockCreateModel timeLockCreateModel) {
		log.debug("update time lock {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var timeLock = timeLockRepository.findById(id).orElseThrow(NotFoundException::new);

				if (!checkUserForOrganization(user, timeLock.getOrganization())) {
					log.warn("user {} not permitted", user.getId());
					throw new NotPermittedException();
				}

				if (!timeLock.getName().equals(timeLockCreateModel.getName())
						&& timeLockRepository.findOneByNameAndOrganizationAndDeletedFalse(timeLockCreateModel.getName(),
								timeLock.getOrganization()).isPresent()) {
					log.warn("time lock {} already exist", timeLockCreateModel.getName());
					return ResponseModel.error(Code.EXIST, "name");
				}
				var from = DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getFromDate());
				var to = DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getToDate());

				log.debug("time create lock {} from ", from);
				log.debug("time create lock {} to ", to);
				log.debug("time  lock {} from ", timeLock.getFromDate());
				log.debug("time  lock {} to ", timeLock.getToDate());

				if (from.equals(to) || from.after(to)) {
					throw new MintException(Code.INVALID, "error.timelock.daterange");
				}
				if ((!new Date(timeLock.getFromDate().getTime()).equals(from)
						|| !new Date(timeLock.getToDate().getTime()).equals(to))
						&& timeLock.getTimeLockExceptions().stream().filter(new Predicate<TimeLockException>() {
							@Override
							public boolean test(TimeLockException e) {
								return !e.isDeleted();
							}
						}).count() > 0) {
					throw new MintException(Code.INVALID, "error.timelock.exception.exists");
				}
				timeLock.setName(timeLockCreateModel.getName());
				timeLock.setFromDate(DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getFromDate()));
				timeLock.setToDate(DateConverter.convertZonedDateTimeToDate(timeLockCreateModel.getToDate()));
				timeLock.getDays().clear();
				timeLockCreateModel.getDayModels().forEach(new Consumer<DayModel>() {
					@Override
					public void accept(DayModel model) {
						timeLock.getDays().put(model.getDay(), String.join(",",
								model.getTimeModels().stream().map(TimeModel::toString).collect(Collectors.toList())));
					}
				});
				// timeLock.setOrganization(timeLock.getOrganization());
				if (timeLockCreateModel.getUnlockPassword() != null) {
					timeLock.setUnlockPassword(timeLockCreateModel.getUnlockPassword());
				}
				timeLockRepository.save(timeLock);
				timeLock.getUsers().clear();
				timeLockRepository.save(timeLock);
				log.debug("time lock updated and users cleared");
				extractUsers(timeLockCreateModel, timeLock.getOrganization(), timeLock);
				timeLockRepository.save(timeLock);
				log.debug("time lock updated with roles");
				return ResponseModel.done();
			}
		}).orElseGet(new Supplier<ResponseModel>() {
			@Override
			public ResponseModel get() {
				log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
				throw new NotPermittedException();
			}
		});
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_UPDATE)
	@PreAuthorize("hasAuthority('TIMELOCK_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel createException(Long id, TimeLockExceptionCreationModel timeLockExceptionCreationModel) {
		log.debug("create time lock exception {} ", id);
		var timeLock = timeLockRepository.findById(id).orElseThrow(NotFoundException::new);

		var timeLockException = new TimeLockException();
		timeLockException.setName(timeLockExceptionCreationModel.getName());
		timeLockException
				.setFromDate(DateConverter.convertZonedDateTimeToDate(timeLockExceptionCreationModel.getFromDate()));
		timeLockException
				.setToDate(DateConverter.convertZonedDateTimeToDate(timeLockExceptionCreationModel.getToDate()));
		timeLockException.setFromTime(timeLockExceptionCreationModel.getFromTime());
		timeLockException.setToTime(timeLockExceptionCreationModel.getToTime());
		timeLockException.setLockStatus(timeLockExceptionCreationModel.getLockStatus());
		timeLockException.setTimeLock(timeLock);
		if (timeLockException.getFromDate().compareTo(timeLock.getFromDate()) <= 0
				|| timeLockException.getToDate().compareTo(timeLock.getToDate()) >= 0) {
			throw new MintException(Code.INVALID, "error.timelocak.exception.range");
		}
		if (timeLock.getTimeLockExceptions().stream().filter(new Predicate<TimeLockException>() {
			@Override
			public boolean test(TimeLockException e) {
				return !e.isDeleted();
			}
		})
				.anyMatch(new Predicate<TimeLockException>() {
					@Override
					public boolean test(TimeLockException e) {
						return e.equals(timeLockException);
					}
				})) {
			log.warn("time lock {} Exception exist", timeLock.getName());
			throw new ExistException("error.timelock.exception.exits");
		}
		timeLockExceptionRepository.save(timeLockException);
		log.debug("time Lock exception created");
		return ResponseModel.done(timeLockException.getId());
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_UPDATE)
	@PreAuthorize("hasAuthority('TIMELOCK_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel updateException(Long id, Long timeLockId,
			TimeLockExceptionCreationModel timeLockExceptionCreationModel) {
		log.debug("update time lock exception {}", id);
		var timeLock = timeLockRepository.findById(timeLockId).orElseThrow(NotFoundException::new);

		var timeLockException = timeLockExceptionRepository.findById(id)
				.orElseThrow(NotFoundException::new);

		if (!timeLock.equals(timeLockException.getTimeLock())) {
			log.warn("invalid time lock exception {} , {}", timeLock, timeLockException.getTimeLock());
			throw new MintException(Code.INVALID, "timeLock");
		}

		timeLockException.setName(timeLockExceptionCreationModel.getName());
		timeLockException
				.setFromDate(DateConverter.convertZonedDateTimeToDate(timeLockExceptionCreationModel.getFromDate()));
		timeLockException
				.setToDate(DateConverter.convertZonedDateTimeToDate(timeLockExceptionCreationModel.getToDate()));
		timeLockException.setFromTime(timeLockExceptionCreationModel.getFromTime());
		timeLockException.setToTime(timeLockExceptionCreationModel.getToTime());
		timeLockException.setLockStatus(timeLockExceptionCreationModel.getLockStatus());
		if (timeLockException.getFromDate().compareTo(timeLock.getFromDate()) <= 0
				|| timeLockException.getToDate().compareTo(timeLock.getToDate()) >= 0) {
			throw new MintException(Code.INVALID, "error.timelocak.exception.range");
		}
		timeLockExceptionRepository.save(timeLockException);
		log.debug("time lock exception updated");
		return ResponseModel.done();
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_UPDATE)
	@PreAuthorize("hasAuthority('TIMELOCK_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel deleteException(Long id, Long timeLockId) {
		log.debug("delete exception {}", id);
		return timeLockExceptionRepository.findOneByIdAndTimeLockIdAndDeletedFalse(id, timeLockId).map(new Function<TimeLockException, ResponseModel>() {
			@Override
			public ResponseModel apply(TimeLockException e) {
				timeLockExceptionRepository.delete(e);
				return ResponseModel.done();
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@Auditable(EntityAction.TIME_LOCK_CREATE)
	@PreAuthorize("hasAuthority('TIMELOCK_CREATE') AND hasAuthority('ADMIN')")
	public ResponseModel duplicate(Long id) {
		log.debug("duplicate time lock {}", id);
		var responseModel = getById(id);
		var timeLockModel = (TimeLockModel) responseModel.getData();
		var organization = organizationRepository.findById(timeLockModel.getOrganizationModel().getId())
				.orElseThrow(NotFoundException::new);
		var lock = new TimeLock();
		lock.setName(timeLockModel.getName() + "(copy)");
		lock.setFromDate(DateConverter.convertZonedDateTimeToDate(timeLockModel.getFromDate()));
		lock.setToDate(DateConverter.convertZonedDateTimeToDate(timeLockModel.getToDate()));
		timeLockModel.getDayModels().forEach(new Consumer<DayModel>() {
			@Override
			public void accept(DayModel model) {
				lock.getDays().put(model.getDay(), String.join(",",
						model.getTimeModels().stream().map(TimeModel::toString).collect(Collectors.toList())));
			}
		});
		lock.setOrganization(organization);

		timeLockRepository.save(lock);
		log.debug("time lock {} duplicated", id);

		Set<TimeLockException> timeLockExceptionSet = timeLockModel.getTimeLockExceptionModels().stream()
				.map(new Function<TimeLockExceptionModel, TimeLockException>() {
					@Override
					public TimeLockException apply(TimeLockExceptionModel timeLockExceptionModel) {
						var timeLockException = new TimeLockException();
						timeLockException.setName(timeLockExceptionModel.getName());
						timeLockException.setFromDate(
								DateConverter.convertZonedDateTimeToDate(timeLockExceptionModel.getFromDate()));
						timeLockException
								.setToDate(DateConverter.convertZonedDateTimeToDate(timeLockExceptionModel.getToDate()));
						timeLockException.setFromTime(timeLockExceptionModel.getFromTime());
						timeLockException.setToTime(timeLockExceptionModel.getToTime());
						timeLockException.setLockStatus(timeLockExceptionModel.getLockStatus());
						timeLockException.setTimeLock(lock);
						return timeLockException;
					}
				}).collect(Collectors.toSet());
		if (!timeLockExceptionSet.isEmpty()) {
			timeLockExceptionRepository.saveAll(timeLockExceptionSet);
			log.debug("time lock exception duplicated");
		}
		// timeLockCreateModel
		return ResponseModel.done(lock.getId());
	}

	private void extractUsers(TimeLockCreateModel timeLockModel, Organization organization, TimeLock lock) {
		Set<User> users;
		if (!timeLockModel.getRoles().isEmpty()) {
			users = userRepository.findByRolesIdInAndOrganizationAndDeletedFalse(timeLockModel.getRoles(), organization)
					.filter(new Predicate<User>() {
						@Override
						public boolean test(User user) {
							return user.getType() == UserType.USER;
						}
					}).map(new Function<User, User>() {
						@Override
						public User apply(User user) {
							user.setTimeLock(lock);
							return user;
						}
					}).collect(Collectors.toSet());
			if (!users.isEmpty()) {
				lock.setRoles(String.join(",",
						timeLockModel.getRoles().stream().map(Object::toString).collect(Collectors.toSet())));
				lock.getUsers().addAll(users);
			}
		} else {
			lock.setRoles(null);
		}
		if (!timeLockModel.getGroups().isEmpty()) {
			users = userRepository
					.findByGroupsIdInAndOrganizationAndDeletedFalse(timeLockModel.getGroups(), organization)
					.filter(new Predicate<User>() {
						@Override
						public boolean test(User user) {
							return user.getType() == UserType.USER;
						}
					}).map(new Function<User, User>() {
						@Override
						public User apply(User user) {
							user.setTimeLock(lock);
							return user;
						}
					}).collect(Collectors.toSet());
			if (!users.isEmpty()) {
				lock.setGroups(String.join(",",
						timeLockModel.getGroups().stream().map(Object::toString).collect(Collectors.toSet())));
				lock.getUsers().addAll(users);
			}
		} else {
			lock.setGroups(null);
		}
		if (!timeLockModel.getUsers().isEmpty()) {
			users = userRepository.findByIdInAndOrganizationAndDeletedFalse(timeLockModel.getUsers(), organization)
					.filter(new Predicate<User>() {
						@Override
						public boolean test(User user) {
							return user.getType() == UserType.USER;
						}
					}).map(new Function<User, User>() {
						@Override
						public User apply(User user) {
							user.setTimeLock(lock);
							return user;
						}
					}).collect(Collectors.toSet());
			if (!users.isEmpty()) {
				lock.setUserIds(String.join(",",
						users.stream().map(new Function<User, String>() {
							@Override
							public String apply(User user) {
								return user.getId().toString();
							}
						}).collect(Collectors.toSet())));
				lock.getUsers().addAll(users);
			}
		} else {
			lock.setUserIds(null);
		}
	}

	@Transactional
	public ResponseModel validateUnlockPassword(String password) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				if (user.getTimeLock() == null || user.getTimeLock().isDeleted()) {
					if (lockPass.equals(password)) {
						return ResponseModel.done();
					}
					throw new MintException(Code.INVALID, "error.timelock.password");
				}
				if (user.getTimeLock().getUnlockPassword() != null && !user.getTimeLock().getUnlockPassword().isEmpty()
						&& user.getTimeLock().getUnlockPassword().equals(password)) {
					return ResponseModel.done();
				}
				throw new MintException(Code.INVALID, "error.timelock.password");
			}
		}).orElseThrow(NotPermittedException::new);
	}

	private TimeLockModel mapTimeLock(TimeLock timeLock) {
		log.debug("Map Time lock  to time lock model");
		var timeLockModel = new TimeLockModel();
		timeLockModel.setId(timeLock.getId());
		timeLockModel.setName(timeLock.getName());
		timeLockModel.setFromDate(DateConverter.convertDateToZonedDateTime(timeLock.getFromDate()));
		timeLockModel.setToDate(DateConverter.convertDateToZonedDateTime(timeLock.getToDate()));
		timeLockModel.setOrganizationModel(
				OrganizationMapper.INSTANCE.mapOrganizationToSimpleOrganizationModel(timeLock.getOrganization()));
		timeLock.getDays().forEach(new BiConsumer<WeekDay, String>() {
			@Override
			public void accept(WeekDay weekDay, String s) {
				timeLockModel.getDayModels().add(new DayModel(weekDay, s));
			}
		});
		if (timeLock.getGroups() != null) {
			timeLockModel.getGroups().addAll(Arrays.stream(timeLock.getGroups().split(",")).filter(new Predicate<String>() {
				@Override
				public boolean test(String s1) {
					return !s1.isEmpty();
				}
			})
					.map(Long::new).collect(Collectors.toSet()));
		}
		if (timeLock.getRoles() != null) {
			timeLockModel.getRoles().addAll(Arrays.stream(timeLock.getRoles().split(",")).filter(new Predicate<String>() {
				@Override
				public boolean test(String s1) {
					return !s1.isEmpty();
				}
			})
					.map(Long::new).collect(Collectors.toSet()));
		}
		if (timeLock.getUserIds() != null) {
			timeLockModel.getUsers().addAll(Arrays.stream(timeLock.getUserIds().split(",")).filter(new Predicate<String>() {
				@Override
				public boolean test(String s1) {
					return !s1.isEmpty();
				}
			})
					.map(Long::new).collect(Collectors.toSet()));
		}
		timeLockModel.setTimeLockExceptionCount(
				Long.valueOf(timeLock.getTimeLockExceptions().stream().filter(new Predicate<TimeLockException>() {
					@Override
					public boolean test(TimeLockException e) {
						return !e.isDeleted();
					}
				}).count()).intValue());
		timeLockModel.setUnlockPassword(timeLock.getUnlockPassword());
		return timeLockModel;
	}

	private TimeLockExceptionModel mapTimeLockException(TimeLockException timeLockException) {
		log.debug("Map Time lock exception  to time lock exception model");
		var timeLockExceptionModel = new TimeLockExceptionModel();
		timeLockExceptionModel.setId(timeLockException.getId());
		timeLockExceptionModel.setName(timeLockException.getName());
		timeLockExceptionModel.setFromDate(DateConverter.convertDateToZonedDateTime(timeLockException.getFromDate()));
		timeLockExceptionModel.setToDate(DateConverter.convertDateToZonedDateTime(timeLockException.getToDate()));
		timeLockExceptionModel.setToTime(timeLockException.getToTime());
		timeLockExceptionModel.setFromTime(timeLockException.getFromTime());
		timeLockExceptionModel.setLockStatus(timeLockException.getLockStatus());
		return timeLockExceptionModel;
	}
}
