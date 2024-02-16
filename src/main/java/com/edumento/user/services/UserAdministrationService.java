package com.edumento.user.services;

import static org.springframework.data.jpa.domain.Specification.where;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.model.role.AssignRoleModel;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.b2b.services.GroupService;
import com.edumento.b2b.services.RoleService;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.GeneralConstant;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.core.util.PermissionCheck;
import com.edumento.core.util.RandomUtils;
import com.edumento.space.domain.Joined;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.services.SpaceService;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserCreateModel;
import com.edumento.user.model.user.UserModel;
import com.edumento.user.model.user.UserOrganizationCreateModel;
import com.edumento.user.repo.UserRepository;
import com.edumento.user.repo.specifications.UserSpecifications;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAdministrationService {
	private final RoleService roleService;

	private final GroupService groupService;

	private final OrganizationRepository organizationRepository;

	private final JoinedRepository joinedRepository;
	private final SpaceService spaceService;

	private final UserRepository userRepository;

	private final FoundationRepository foundationRepository;

	private final PasswordEncoder passwordEncoder;

	public UserAdministrationService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			FoundationRepository foundationRepository, RoleService roleService, GroupService groupService,
			OrganizationRepository organizationRepository, JoinedRepository joinedRepository,
			SpaceService spaceService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.foundationRepository = foundationRepository;
		this.roleService = roleService;
		this.groupService = groupService;
		this.organizationRepository = organizationRepository;
		this.joinedRepository = joinedRepository;
		this.spaceService = spaceService;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAnyAuthority('USER_READ','USER_SYSTEM_ADMIN_READ','USER_FOUNDATION_ADMIN_READ','USER_ADMIN_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getUsers(PageRequest page, Long foundationId, Long organizationId, UserType type,
			String filter, boolean all) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			log.debug("get Users");
			Specification<User> byUserType = null;
			Specification<User> byOrganization = null;
			Specification<User> byFoundation = null;
			Specification<User> filterKey = null;

			if (foundationId != null) {
				Optional<Foundation> foundationIns = foundationRepository.findOneByIdAndDeletedFalse(foundationId);
				if (foundationIns.isPresent()) {
					byFoundation = UserSpecifications.inFoundation(foundationIns.get());
				} else {
					log.warn("foundation {} not found", foundationId);
					throw new NotFoundException("role");
				}
			} else {
				byFoundation = all ? null : (root, cq, cb) -> cb.isNull(root.get("foundation"));
			}

			if (organizationId != null) {
				Optional<Organization> org = organizationRepository.findOneByIdAndDeletedFalse(organizationId);
				if (org.isPresent()) {
					byOrganization = UserSpecifications.inOrganization(org.get());
					if (foundationId == null) {
						byFoundation = UserSpecifications.inFoundation(org.get().getFoundation());
					}
				} else {
					log.warn("organization {} not found", organizationId);
					throw new NotFoundException("organization");
				}
			} else {

				byOrganization = all ? null : (root, cq, cb) -> cb.isNull(root.get("organization"));
			}

			if (type != null) {

				byUserType = UserSpecifications.hasUserType(type);
			}

			if (filter != null) {
				if (filter.matches(GeneralConstant.EMAIL_PATTERN)) {
					filterKey = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"),
							filter);
				} else {
					filterKey = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.or(
							criteriaBuilder.like(criteriaBuilder.lower(root.get("userName").as(String.class)),
									"%" + filter.toLowerCase() + "%"),
							criteriaBuilder.like(criteriaBuilder.lower(root.get("fullName").as(String.class)),
									"%" + filter.toLowerCase() + "%"));
				}
			}
			switch (user.getType()) {
			case SUPER_ADMIN:
				if (byUserType == null) {
					byUserType = UserSpecifications.hasUserType(UserType.values());
				}
				break;
			case SYSTEM_ADMIN:
				if (byUserType == null) {
					byUserType = UserSpecifications.hasUserType(UserType.FOUNDATION_ADMIN, UserType.ADMIN,
							UserType.USER);
				}
				break;
			case ADMIN:
				byFoundation = UserSpecifications.inFoundation(user.getFoundation());
				byOrganization = UserSpecifications.inOrganization(user.getOrganization());
				byUserType = UserSpecifications.hasUserType(UserType.USER);
				break;
			case FOUNDATION_ADMIN:
				byFoundation = UserSpecifications.inFoundation(user.getFoundation());
				byUserType = UserSpecifications.hasUserType(UserType.USER, UserType.ADMIN);
				log.debug("foundation ADMIN COLLECTED");
				break;
			default:
				log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
				throw new NotPermittedException();
			}

			Page<UserModel> userModels = userRepository.findAll(where(byUserType).and(byOrganization).and(byFoundation)
					.and(filterKey).and(UserSpecifications.notDeleted()), page).map(UserModel::new);
			return PageResponseModel.done(userModels.getContent(), userModels.getTotalPages(), userModels.getNumber(),
					userModels.getTotalElements());
		}).orElseThrow(NotPermittedException::new);
	}

	@Deprecated
	@PreAuthorize("hasAnyAuthority('USER_READ') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel getCloudUsers(PageRequest pageRequest) {
		Page<UserModel> userInfoModelPage = userRepository
				.findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse(pageRequest).map(UserModel::new);
		return PageResponseModel.done(userInfoModelPage.getContent(), userInfoModelPage.getTotalPages(),
				userInfoModelPage.getNumber(), userInfoModelPage.getTotalElements());
	}

	@Deprecated
	@PreAuthorize("hasAnyAuthority('USER_READ') AND hasAuthority('SUPER_ADMIN')")
	public ResponseModel getSystemAdmins(PageRequest pageRequest) {
		Page<UserModel> userInfoModelPage = userRepository
				.findAllByTypeAndDeletedFalse(UserType.SYSTEM_ADMIN, pageRequest).map(UserModel::new);
		return PageResponseModel.done(userInfoModelPage.getContent(), userInfoModelPage.getTotalPages(),
				userInfoModelPage.getNumber(), userInfoModelPage.getTotalElements());
	}

	@Auditable(EntityAction.USER_UPDATE)
	@PreAuthorize("hasAnyAuthority('USER_UPDATE','SYSTEMADMIN_UPDATE','FOUNDATIONADMIN_UPDATE','ORGADMIN_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel updateUserInformation(UserOrganizationCreateModel userUpdateModel, Long Id) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> userRepository.findOneByIdAndDeletedFalse(Id).map(u -> {
					if (user.getType() != UserType.SUPER_ADMIN && user.getType() != UserType.SYSTEM_ADMIN) {
						if ((user.getType() == UserType.FOUNDATION_ADMIN
								&& !user.getFoundation().equals(u.getFoundation()))
								&& (user.getType() == UserType.ADMIN
										&& user.getOrganization().equals(u.getOrganization()))
								&& user.getType() == UserType.USER) {
							throw new NotPermittedException();
						}
					}
					u.setFullName(userUpdateModel.getFullName());
					u.setBirthDate(DateConverter.convertZonedDateTimeToDate(userUpdateModel.getBirthDate()));
					u.setCountry(userUpdateModel.getCountry());
					u.setProfession(userUpdateModel.getProfession());
					u.setMobile(userUpdateModel.getMobile());
					u.setThumbnail(userUpdateModel.getImage());
					u.setNotification(userUpdateModel.getNotification());
					u.setMailNotification(userUpdateModel.getEmailNotification());
					u.setGender(userUpdateModel.getGender() == null ? null : userUpdateModel.getGender().getValue());
					if (userUpdateModel.getEmail() != null
							&& !u.getEmail().equalsIgnoreCase(userUpdateModel.getEmail())) {
						if (userRepository.findOneByEmailAndDeletedFalse(userUpdateModel.getEmail()).isPresent()) {
							log.warn("email {} exist", userUpdateModel.getEmail());
							throw new ExistException("email");
						}
						u.setEmail(userUpdateModel.getEmail());
					}
					if (userUpdateModel.getOrganizationId() != null && u.getOrganization() != null
							&& !userUpdateModel.getOrganizationId().equals(u.getOrganization().getId())) {
						Organization organization = organizationRepository.findById(userUpdateModel.getOrganizationId())
								.orElse(null);
						if (organization != null) {
							if (u.getSpaces().stream().filter(space -> !space.isDeleted()).count() > 0) {
								throw new MintException(Code.INVALID,
										"user has spaces cannot be moved to another organization");
							} else {
								u.setOrganization(organization);
								u.getGroups().forEach(groups -> groupService
										.removeUserFromGroup(Collections.singletonList(u.getId()), groups.getId()));
								roleService.removeRolesFromUser(u);
							}
						}
					}
					userRepository.save(u);
					log.debug("user updated");

					if (userUpdateModel.getGroups() != null && !userUpdateModel.getGroups().isEmpty()) {
						u.getGroups().stream()
								.filter(groupFilter -> !userUpdateModel.getGroups().contains(groupFilter.getId()))
								.forEach(groups -> groupService
										.removeUserFromGroup(Collections.singletonList(u.getId()), groups.getId()));
						boolean exist = false;
						for (Long aLong : userUpdateModel.getGroups()) {
							for (Groups group : u.getGroups()) {
                                if (aLong.equals(group.getId())) {
                                    exist = true;
                                    break;
                                }
							}
							if (!exist) {
								groupService.assignUserToGroup(Collections.singletonList(u.getId()), aLong);
							}
						}
					}

					if (userUpdateModel.getRoleId() != null) {
						roleService.assignRole(new AssignRoleModel(userUpdateModel.getRoleId(), u.getId()));
					}
					log.debug("Changed Information for User: {}", u);
					return ResponseModel.done();
				}).orElseThrow(() -> new NotFoundException("user"))).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.USER_CREATE)
	@PreAuthorize("hasAnyAuthority('USER_CREATE','SYSTEMADMIN_CREATE','FOUNDATIONADMIN_CREATE','ORGADMIN_CREATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.USER_CREATE, services = Services.NOTIFICATIONS)
	public ResponseModel createUser(UserOrganizationCreateModel userCreateModel, String baseUrl) {
		log.debug("create user: {}", userCreateModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(u -> {
			Object[] objects = null;

			switch (userCreateModel.getType()) {
			case SYSTEM_ADMIN:
				objects = createSystemAdmin(userCreateModel);
				break;
			case FOUNDATION_ADMIN:
				objects = createFoundationAdmin(u, userCreateModel);
				break;
			case ADMIN:
				objects = createOrganizationAdmin(u, userCreateModel);
				break;
			case USER:
				objects = createUser(u, userCreateModel);
				break;
			default:
				throw new NotPermittedException();
			}

			User newUser = ((User) objects[0]);
			String password = String.valueOf(objects[1]);
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
					.getRequest();
			String lang = "en";
			if (request.getHeader("lang") != null) {
				lang = request.getHeader("lang");
			}

			log.debug("Created Information for User: {}", objects[0]);
			return ResponseModel.done(null, new UserInfoMessage(newUser, password));
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.USER_UPDATE)
	@PreAuthorize("hasAnyAuthority('USER_UPDATE','SYSTEMADMIN_UPDATE','FOUNDATIONADMIN_UPDATE','ORGADMIN_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel restPassword(Long id) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> userRepository.findOneByIdAndDeletedFalse(id).map(u -> {
					PermissionCheck.checkUserForFoundationAndOrgOperation(user,
							u.getOrganization() != null ? u.getOrganization().getId() : null,
							u.getFoundation() != null ? u.getFoundation().getId() : null);
					String password = RandomUtils.generatePassword();
					String encryptedPassword = passwordEncoder.encode(password);
					u.setPassword(encryptedPassword);
					userRepository.save(u);
					log.debug("Changed password for User: {}", u);
					return ResponseModel.done((Object) password);
				}).orElseThrow(() -> new NotFoundException("user"))).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.USER_DELETE)
	@PreAuthorize("hasAnyAuthority('USER_DELETE','SYSTEMADMIN_DELETE','FOUNDATIONADMIN_DELETE','ORGADMIN_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel deleteUserInformation(Long id) {
		log.debug("delete user {} information", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> userRepository.findOneByIdAndDeletedFalse(id).map(u -> {
					PermissionCheck.checkUserForFoundationAndOrgOperation(user,
							u.getOrganization() != null ? u.getOrganization().getId() : null,
							u.getFoundation() != null ? u.getFoundation().getId() : null);
					u.getGroups().forEach(groups -> groupService
							.removeUserFromGroup(Collections.singletonList(u.getId()), groups.getId()));
					roleService.removeRolesFromUser(u);
					Set<Joined> joineds = joinedRepository.findByUserIdAndDeletedFalse(u.getId())
							.collect(Collectors.toSet());
					if (joineds != null && !joineds.isEmpty()) {
						joinedRepository.deleteAll(joineds);
					}
					u.getSpaces().stream().filter(space -> !space.isDeleted())
							.forEach(space -> spaceService.deleteSpace(space.getId()));
					userRepository.delete(u);
					return ResponseModel.done();
				}).orElseThrow(() -> new NotFoundException("user"))).orElseThrow(NotPermittedException::new);
	}

	public void deleteUserInOrganization(Long id) {
		log.debug("delete users in organization {}", id);
		Set<User> users = userRepository.findByOrganizationIdAndDeletedFalseAndOrganizationDeletedFalse(id)
				.collect(Collectors.toSet());
		if (!users.isEmpty()) {
			userRepository.deleteAll(users);
			log.debug("users in organization {} deleted", id);
		}
	}

	@Transactional
	@Auditable(EntityAction.USER_UPDATE)
	@PreAuthorize("hasAuthority('USER_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel changeUserStatus(Long id) {
		return userRepository.findOneByIdAndDeletedFalse(id).map(user -> {
			if (user.getType() == UserType.SUPER_ADMIN
					|| (user.getType() == UserType.SYSTEM_ADMIN
							&& !SecurityUtils.isCurrentUserInRole("SYSTEMADMIN_UPDATE"))
					|| (user.getType() == UserType.FOUNDATION_ADMIN
							&& !SecurityUtils.isCurrentUserInRole("FOUNDATIONADMIN_UPDATE"))
					|| (user.getType() == UserType.ADMIN && !SecurityUtils.isCurrentUserInRole("ORGADMIN_UPDATE"))) {
				throw new NotPermittedException();
			}
			user.setStatus(!user.getStatus());
			userRepository.save(user);
			return ResponseModel.done();
		}).orElseThrow(() -> new NotFoundException("user"));
	}

	@PreAuthorize("hasAuthority('SYSTEMADMIN_CREATE') and hasAuthority('SUPER_ADMIN')")
	private Object[] createSystemAdmin(UserCreateModel userCreateModel) {
		Map<String, String> exist = new HashMap<>();
		if (userRepository.findOneByUserNameAndDeletedFalse(userCreateModel.getUsername()).isPresent()) {
			exist.put("username", "exist");
		}

		if (userRepository.findOneByEmailAndDeletedFalse(userCreateModel.getEmail()).isPresent()) {
			exist.put("email", "exist");
		}
		if (!exist.isEmpty()) {
			log.warn("user {} already exist", exist);
			throw new ExistException(exist);
		}

		User user = new User();
		user.setUserName(userCreateModel.getUsername());
		user.setFullName(userCreateModel.getFullName());
		user.setEmail(userCreateModel.getEmail());
		user.setLangKey(userCreateModel.getLang()); // default language is English
		user.setType(userCreateModel.getType());
		user.setThumbnail(userCreateModel.getImage());
		String password = RandomUtils.generatePassword();
		String encryptedPassword = passwordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtils.generateResetKey());
		user.setResetDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now(ZoneOffset.UTC)));
		user.setStatus(true);
		user.setFirstLogin(true);
		user.setForceChangePassword(true);
		user.setBirthDate(DateConverter.convertZonedDateTimeToDate(userCreateModel.getBirthDate()));
		user.setCountry(userCreateModel.getCountry());
		user.setGender(userCreateModel.getGender().getValue());
		user.setProfession(userCreateModel.getProfession());
		user.setStartDate(user.getCreationDate());
		user.setMobile(userCreateModel.getMobile());
		user.setUserStatus(userCreateModel.getUserStatus());
		user.setInterests(userCreateModel.getInterests());
		userRepository.save(user);
		return new Object[] { user, password };
	}

	@PreAuthorize("hasAuthority('FOUNDATIONADMIN_CREATE') and hasAuthority('SYSTEM_ADMIN')")
	private Object[] createFoundationAdmin(User u, UserOrganizationCreateModel userCreateModel) {

		Map<String, String> exist = new HashMap<>();

		Foundation foundation = userCreateModel.getFoundationId() != null
				? foundationRepository.findById(userCreateModel.getFoundationId()).orElse(u.getFoundation())
				: u.getFoundation();

		if (foundation == null) {
			throw new MintException(Code.INVALID, "foundation");
		}

		if (Objects.equals(userRepository.countByFoundationAndDeletedFalse(foundation),
				foundation.getFoundationPackage().getNumberOfUsers())) {
			log.warn("reach organization limit");
			throw new InvalidException("error.foundation.org.limit");
		} else if (ZonedDateTime.now(ZoneOffset.UTC)
				.isAfter(ZonedDateTime.ofInstant(foundation.getEndDate().toInstant(), ZoneOffset.UTC))
				|| !foundation.getActive()) {
			if (foundation.getActive()) {
				foundation.setActive(false);
				foundationRepository.save(foundation);
			}
			log.warn("foundation not active");
			throw new MintException(Code.INVALID, "error.foundation.active");
		}

		String userName = String.format("%s@%s", userCreateModel.getUsername(), foundation.getCode());
		if (userRepository.findOneByUserNameAndDeletedFalse(userName).isPresent()) {
			exist.put("username", "exist");
		}

		if (userRepository.findOneByEmailAndDeletedFalse(userCreateModel.getEmail()).isPresent()) {
			exist.put("email", "exist");
		}
		if (!exist.isEmpty()) {
			log.warn("user {} already exist", exist);
			throw new ExistException(exist);
		}

		User user = new User();
		user.setUserName(userName);
		user.setFullName(userCreateModel.getFullName());
		user.setEmail(userCreateModel.getEmail());
		user.setLangKey("en"); // default language is English
		user.setType(userCreateModel.getType());
		user.setThumbnail(userCreateModel.getImage());
		String password = RandomUtils.generatePassword();
		String encryptedPassword = passwordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtils.generateResetKey());
		user.setResetDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now(ZoneOffset.UTC)));
		user.setStatus(true);
		user.setFirstLogin(true);
		user.setForceChangePassword(true);
		user.setBirthDate(DateConverter.convertZonedDateTimeToDate(userCreateModel.getBirthDate()));
		user.setCountry(userCreateModel.getCountry());
		user.setGender(userCreateModel.getGender().getValue());
		user.setProfession(userCreateModel.getProfession());
		user.setStartDate(user.getCreationDate());
		user.setMobile(userCreateModel.getMobile());
		user.setEndDate(foundation.getEndDate());
		user.setUserStatus(userCreateModel.getUserStatus());
		user.setInterests(userCreateModel.getInterests());
		user.setFoundation(foundation);
		userRepository.save(user);
		if (userCreateModel.getRoleId() != null) {
			roleService.assignRole(new AssignRoleModel(userCreateModel.getRoleId(), user.getId()));
		}

		if (userCreateModel.getGroups() != null) {
			userCreateModel.getGroups()
					.forEach(aLong -> groupService.assignUserToGroup(Collections.singletonList(user.getId()), aLong));
		}

		log.debug("Created Information for User: {}", user);
		return new Object[] { user, password };
	}

	@PreAuthorize("hasAuthority('ORGADMIN_CREATE') and hasAuthority('FOUNDATION_ADMIN')")
	private Object[] createOrganizationAdmin(User u, UserOrganizationCreateModel userCreateModel) {

		log.debug("create orgadmin user: {}", userCreateModel);

		Map<String, String> exist = new HashMap<>();

		Organization organization = userCreateModel.getOrganizationId() != null
				? organizationRepository.findById(userCreateModel.getOrganizationId()).orElse(u.getOrganization())
				: u.getOrganization();

		if (organization == null) {
			throw new MintException(Code.INVALID, "organization");
		}
		if (u.getType() == UserType.FOUNDATION_ADMIN
				&& !u.getFoundation().getId().equals(organization.getFoundation().getId())) {
			throw new NotPermittedException();
		}

		checkFoundationCapacity(organization.getFoundation());

		String userName = String.format("%s@%s", userCreateModel.getUsername(), organization.getOrgId());
		if (userRepository.findOneByUserNameAndDeletedFalse(userName).isPresent()) {
			exist.put("username", "exist");
		}

		if (userRepository.findOneByEmailAndDeletedFalse(userCreateModel.getEmail()).isPresent()) {
			exist.put("email", "exist");
		}
		if (!exist.isEmpty()) {
			log.warn("user {} already exist", exist);
			throw new ExistException(exist);
		}
		User user = new User();
		user.setUserName(userName);
		user.setFullName(userCreateModel.getFullName());
		user.setEmail(userCreateModel.getEmail());
		user.setLangKey(userCreateModel.getLang()); // default language is English
		user.setType(userCreateModel.getType());
		user.setThumbnail(userCreateModel.getImage());
		String password = RandomUtils.generatePassword();
		String encryptedPassword = passwordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		user.setResetKey(RandomUtils.generateResetKey());
		user.setResetDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now(ZoneOffset.UTC)));
		user.setStatus(true);
		user.setFirstLogin(true);
		user.setForceChangePassword(true);
		user.setBirthDate(DateConverter.convertZonedDateTimeToDate(userCreateModel.getBirthDate()));
		user.setCountry(userCreateModel.getCountry());
		user.setGender(userCreateModel.getGender().getValue());
		user.setProfession(userCreateModel.getProfession());
		user.setStartDate(user.getCreationDate());
		user.setMobile(userCreateModel.getMobile());
		user.setUserStatus(userCreateModel.getUserStatus());
		user.setInterests(userCreateModel.getInterests());
		user.setFoundation(organization.getFoundation());
		user.setOrganization(organization);
		user.setEndDate(organization.getFoundation().getEndDate());
		userRepository.save(user);
		if (userCreateModel.getRoleId() != null) {
			roleService.assignRole(new AssignRoleModel(userCreateModel.getRoleId(), user.getId()));
		}

		if (userCreateModel.getGroups() != null) {
			userCreateModel.getGroups()
					.forEach(aLong -> groupService.assignUserToGroup(Collections.singletonList(user.getId()), aLong));
		}
		return new Object[] { user, password };
	}

	@PreAuthorize("hasAuthority('USER_CREATE') and hasAuthority('ADMIN')")
	private Object[] createUser(User u, UserOrganizationCreateModel userCreateModel) {
		{
			Organization organization = null;
			Map<String, String> exist = new HashMap<>();

			Foundation foundation = userCreateModel.getFoundationId() != null
					? foundationRepository.findById(userCreateModel.getFoundationId()).orElse(u.getFoundation())
					: u.getFoundation();

			if (userCreateModel.getOrganizationId() != null) {
				organization = organizationRepository.findById(userCreateModel.getOrganizationId()).orElse(u.getOrganization());
			}

			if (organization != null && foundation != null
					&& !foundation.getId().equals(organization.getFoundation().getId())) {
				throw new NotPermittedException();
			}

			if (organization == null && foundation == null) {
				throw new MintException(Code.INVALID, "organization");
			}

			if ((organization != null && foundation == null)) {
				throw new NotPermittedException();
			}

			checkFoundationCapacity(foundation);
			if (organization != null && organization.getActive() != null && !organization.getActive()) {
				throw new MintException(Code.INVALID, "error.organization.active");
			}

			String userName = (organization != null)
					? String.format("%s@%s", userCreateModel.getUsername(), organization.getOrgId())
					: String.format("%s@%s", userCreateModel.getUsername(), foundation.getCode());

			if (userRepository.findOneByUserNameAndDeletedFalse(userName).isPresent()) {
				exist.put("username", "exist");
			}

			if (userRepository.findOneByEmailAndDeletedFalse(userCreateModel.getEmail()).isPresent()) {
				exist.put("email", "exist");
			}

			if (!exist.isEmpty()) {
				log.warn("user {} already exist", exist);
				throw new ExistException(exist);
			}

			User user = new User();
			user.setUserName(userName);
			user.setFullName(userCreateModel.getFullName());
			user.setEmail(userCreateModel.getEmail());
			user.setLangKey(userCreateModel.getLang());
			user.setType(userCreateModel.getType());
			user.setThumbnail(userCreateModel.getImage());
			String password = RandomUtils.generatePassword();
			String encryptedPassword = passwordEncoder.encode(password);
			user.setPassword(encryptedPassword);
			user.setResetKey(RandomUtils.generateResetKey());
			user.setResetDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now(ZoneOffset.UTC)));
			user.setStatus(true);
			user.setFirstLogin(true);
			user.setForceChangePassword(true);
			user.setBirthDate(DateConverter.convertZonedDateTimeToDate(userCreateModel.getBirthDate()));
			user.setCountry(userCreateModel.getCountry());
			user.setGender(userCreateModel.getGender().getValue());
			user.setProfession(userCreateModel.getProfession());
			user.setStartDate(user.getCreationDate());
			user.setMobile(userCreateModel.getMobile());
			user.setUserStatus(userCreateModel.getUserStatus());
			user.setInterests(userCreateModel.getInterests());
			if (organization != null) {
				user.setEndDate(organization.getEndDate());
				user.setOrganization(organization);
			} else {
				user.setEndDate(foundation.getEndDate());
			}
			user.setFoundation(foundation);
			userRepository.save(user);

			if (userCreateModel.getRoleId() != null) {
				roleService.assignRole(new AssignRoleModel(userCreateModel.getRoleId(), user.getId()));
			}

			if (userCreateModel.getGroups() != null) {
				userCreateModel.getGroups().forEach(
						aLong -> groupService.assignUserToGroup(Collections.singletonList(user.getId()), aLong));
			}

			log.debug("Created Information for User: {}", user);
			return new Object[] { user, password };
		}
	}

	private void checkFoundationCapacity(Foundation foundation) {
		if (userRepository.countByFoundationAndDeletedFalse(foundation)
				.compareTo(foundation.getFoundationPackage().getNumberOfUsers()) >= 0) {
			log.warn("reach organization limit");
			throw new InvalidException("error.foundation.org.limit");
		} else if (ZonedDateTime.now(ZoneOffset.UTC)
				.isAfter(ZonedDateTime.ofInstant(foundation.getEndDate().toInstant(), ZoneOffset.UTC))
				|| !foundation.getActive()) {
			if (Boolean.TRUE.equals(foundation.getActive())) {
				foundation.setActive(false);
				foundationRepository.save(foundation);
			}
			log.warn("organization not active");
			throw new MintException(Code.INVALID, "error.foundation.active");
		}
	}

	@Transactional
	@PostConstruct
	protected void userInitializer() {
		userRepository.findOneByUserNameAndDeletedFalse("admin").orElseGet(() -> {
			User user2 = new User();
			user2.setUserName("admin");
			user2.setFullName("mint adminstraor");
			user2.setType(UserType.SUPER_ADMIN);
			user2.setEmail("admin@edu-tek.net");
			user2.setStatus(Boolean.TRUE);
			user2.setPassword(passwordEncoder.encode("P@ssw0rd"));
			return userRepository.save(user2);
		});
	}
}
