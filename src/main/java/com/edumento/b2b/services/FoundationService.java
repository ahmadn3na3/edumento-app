package com.edumento.b2b.services;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.b2b.model.foundation.FoundationCreateModel;
import com.edumento.b2b.model.foundation.FoundationModel;
import com.edumento.b2b.repo.FoundationPackageRepository;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.category.services.CategoryService;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.GeneralConstant;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.space.services.SpaceService;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserModel;
import com.edumento.user.repo.UserRepository;

/** Created by ayman on 02/06/16. */
@Service
public class FoundationService {
	private final Logger log = LoggerFactory.getLogger(FoundationService.class);

	private final FoundationRepository foundationRepository;

	private final OrganizationService organizationService;

	private final OrganizationRepository organizationRepository;

	private final CategoryService categoryService;

	private final SpaceService spaceService;

	private final RoleService roleService;

	private final UserRepository userRepository;

	private final FoundationPackageRepository foundationPackageRepository;

	@Autowired
	public FoundationService(FoundationRepository foundationRepository, OrganizationService organizationService,
			OrganizationRepository organizationRepository, CategoryService categoryService, SpaceService spaceService,
			RoleService roleService, UserRepository userRepository,
			FoundationPackageRepository foundationPackageRepository) {
		this.foundationRepository = foundationRepository;
		this.organizationService = organizationService;
		this.organizationRepository = organizationRepository;
		this.categoryService = categoryService;
		this.spaceService = spaceService;
		this.roleService = roleService;
		this.userRepository = userRepository;
		this.foundationPackageRepository = foundationPackageRepository;
	}

	@Transactional
	@Auditable(EntityAction.FOUNDATION_CREATE)
	@PreAuthorize("hasAuthority('FOUNDATION_CREATE') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel create(FoundationCreateModel foundationCreateModel) {
		log.debug("Create Foundation {}", foundationCreateModel);
		if (foundationRepository.findOneByNameAndDeletedFalse(foundationCreateModel.getName()).isPresent()) {
			throw new ExistException("name");
		}

		if (foundationRepository.findOneByCodeAndDeletedFalse(foundationCreateModel.getCode()).isPresent()
				|| organizationRepository.findOneByOrgIdAndDeletedFalse(foundationCreateModel.getCode()).isPresent()) {
			throw new ExistException("code");
		}

		var foundationPackage = foundationPackageRepository
				.findById(foundationCreateModel.getFoundationPackageId()).orElseThrow(NotFoundException::new);

		if (!foundationCreateModel.getCode().matches(GeneralConstant.CODE_PATTERN)) {
			throw new InvalidException("error.code.invaild");
		}
		var foundation = new Foundation();
		foundation.setName(foundationCreateModel.getName());
		foundation.setFoundationPackage(foundationPackage);
		foundation.setGenderSensitivity(foundationCreateModel.getGenderSensitivity());
		foundation.setCode(foundationCreateModel.getCode());
		foundation.setStartDate(DateConverter.convertZonedDateTimeToDate(foundationCreateModel.getStartDate()));
		foundation.setEndDate(DateConverter.convertZonedDateTimeToDate(foundationCreateModel.getEndDate()));
		foundation.setActive(Boolean.TRUE);
		foundationRepository.save(foundation);
		log.debug("foundation created");
		return ResponseModel.done(foundation.getId());
	}

	@Transactional
	@Auditable(EntityAction.FOUNDATION_UPDATE)
	@PreAuthorize("hasAuthority('FOUNDATION_UPDATE') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel update(Long id, FoundationCreateModel foundationCreateModel) {
		log.debug("Update Foundation {}", foundationCreateModel);
		var foundation = foundationRepository.findById(id).orElseThrow(NotFoundException::new);

		if (!foundationCreateModel.getName().equals(foundation.getName())) {
			var tempFoundation = foundationRepository.findByNameAndDeletedFalse(foundationCreateModel.getName());
			if (tempFoundation != null && !id.equals(tempFoundation.getId())) {
				throw new ExistException("foundation");
			}
		}

		foundation.setName(foundationCreateModel.getName());
		foundation.setStartDate(DateConverter.convertZonedDateTimeToDate(foundationCreateModel.getStartDate()));
		foundation.setEndDate(DateConverter.convertZonedDateTimeToDate(foundationCreateModel.getEndDate()));
		// TODO: Flags update in organizations
		foundation.setGenderSensitivity(foundationCreateModel.getGenderSensitivity());
		var foundationPackage = foundationPackageRepository
				.findById(foundationCreateModel.getFoundationPackageId()).orElseThrow(NotFoundException::new);
		if (foundationPackage != null && (foundation.getFoundationPackage() == null
				|| !foundationPackage.getId().equals(foundation.getFoundationPackage().getId()))) {
			foundation.setFoundationPackage(foundationPackage);
		}

		var endDate = foundation.getEndDate();
		List<User> users = new ArrayList<>();
		List<Organization> organizations = new ArrayList<>();

		// update user end date by foundation end date
		userRepository.findByFoundationAndDeletedFalse(foundation).forEach(new Consumer<User>() {
			@Override
			public void accept(User user) {
				user.setEndDate(endDate);
				users.add(user);
			}
		});
		if (!users.isEmpty()) {
			userRepository.saveAll(users);
		}

		// update organization end date by foundation end date
		organizationRepository.findByFoundationIdAndDeletedFalse(foundation.getId()).forEach(new Consumer<Organization>() {
			@Override
			public void accept(Organization organization) {
				organization.setEndDate(endDate);
				organizations.add(organization);
			}
		});
		if (!organizations.isEmpty()) {
			organizationRepository.saveAll(organizations);
		}

		foundationRepository.save(foundation);
		log.debug("foundation {} updated", id);
		return ResponseModel.done();
	}

	@PreAuthorize("hasAnyAuthority('SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getFoundation(Long id) {
		log.debug("get Foundation with id{}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				if (user.getType() == UserType.FOUNDATION_ADMIN && !user.getFoundation().getId().equals(id)) {
					throw new NotPermittedException();
				}
				return foundationRepository.findOneByIdAndDeletedFalse(id)
						.map(new Function<Foundation, ResponseModel>() {
							@Override
							public ResponseModel apply(Foundation foundation) {
								return ResponseModel.done(getFoundationModel(foundation));
							}
						})
						.orElseThrow(NotFoundException::new);
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('FOUNDATION_READ') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel getAllFoundation() {
		log.debug("get All Foundations");
		return ResponseModel.done(
				foundationRepository.findAll().stream().map(this::getFoundationModel).collect(Collectors.toList()));
	}

	private FoundationModel getFoundationModel(Foundation foundation) {
		var foundationModel = new FoundationModel();
		foundationModel.setId(foundation.getId());
		foundationModel.setName(foundation.getName());
		foundationModel.setCode(foundation.getCode());
		foundationModel.setStartDate(DateConverter.convertDateToZonedDateTime(foundation.getStartDate()));
		foundationModel.setEndDate(DateConverter.convertDateToZonedDateTime(foundation.getEndDate()));
		foundationModel.setGenderSensitivity(foundation.getGenderSensitivity());
		if (foundation.getFoundationPackage() != null) {
			foundationModel.setOrganizationsCapacity(foundation.getFoundationPackage().getNumberOfOrganizations());
			foundationModel.setUsersCapacity(foundation.getFoundationPackage().getNumberOfUsers());
			foundationModel.setOrganizationsCount(organizationRepository.countByFoundationAndDeletedFalse(foundation));
			foundationModel.setUsersCount(userRepository.countByFoundationAndDeletedFalse(foundation));
			foundationModel.setFoundationPackageId(foundation.getFoundationPackage().getId());
		}
		foundationModel.setCreationDate(DateConverter.convertDateToZonedDateTime(foundation.getCreationDate()));
		if (foundation.getLastModifiedDate() != null) {
			foundationModel
					.setLastModifiedDate(DateConverter.convertDateToZonedDateTime(foundation.getLastModifiedDate()));
		}
		log.debug("foundation: {}", foundationModel);
		return foundationModel;
	}

	@Auditable(EntityAction.FOUNDATION_DELETE)
	@Transactional
	@PreAuthorize("hasAuthority('FOUNDATION_DELETE') AND hasAuthority('SYSTEM_ADMIN')")
	public ResponseModel delete(Long id) {
		log.debug("delete foundation with id :{},", id);
		if (null != id) {
			return foundationRepository.findOneByIdAndDeletedFalse(id).map(new Function<Foundation, ResponseModel>() {
				@Override
				public ResponseModel apply(Foundation foundation) {
					organizationService.delete(foundation.getOrganizations().stream()
							.filter(new Predicate<Organization>() {
								@Override
								public boolean test(Organization organization) {
									return !organization.isDeleted();
								}
							}).collect(Collectors.toSet()));
					List<User> users = userRepository.findByFoundationAndDeletedFalse(foundation)
							.collect(Collectors.toList());
					if (!users.isEmpty()) {
						userRepository.deleteAll(
								userRepository.findByFoundationAndDeletedFalse(foundation).collect(Collectors.toList()));
					}
					if (!foundation.getRoles().isEmpty()) {
						roleService.delete(foundation.getRoles().stream().filter(new Predicate<Role>() {
							@Override
							public boolean test(Role role) {
								return !role.isDeleted();
							}
						}).map(Role::getId)
								.collect(Collectors.toList()));
					}
					spaceService.deleteSpacesInFoundation(foundation);
					if (!foundation.getCategories().isEmpty()) {
						categoryService.deleteInFoundation(foundation);
					}
					foundationRepository.delete(foundation);
					log.debug("foundation {} deleted", id);
					return ResponseModel.done();
				}
			}).orElseThrow(NotFoundException::new);
		}
		log.warn("id parameter cant be null");
		throw new MintException(Code.INVALID, "id");
	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getSpacesByFoundationId(Long id) {
		return foundationRepository.findOneByIdAndDeletedFalse(id).map(spaceService::getSpacesByFoundation)
				.orElseThrow(NotFoundException::new);
	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getCategoriesByFoundationId(Long id) {
		return foundationRepository.findOneByIdAndDeletedFalse(id).map(categoryService::getCategoriesByFoundation)
				.orElseThrow(NotFoundException::new);
	}

	@Transactional
	@Auditable(EntityAction.FOUNDATION_UPDATE)
	@PreAuthorize("hasAuthority('FOUNDATION_UPDATE') AND hasAnyAuthority('SYSTEM_ADMIN')")
	public ResponseModel changeFoundationStatus(Long id) {
		log.debug("change organization {} status", id);
		return foundationRepository.findOneByIdAndDeletedFalse(id).map(new Function<Foundation, ResponseModel>() {
			@Override
			public ResponseModel apply(Foundation foundation) {
				if ("mint".equals(foundation.getName())) {
					log.warn("Not permitted to change MINT Status");
					throw new NotPermittedException();
				}
				foundation.setActive(!foundation.getActive());
				foundation.getUsers().forEach(new Consumer<User>() {
					@Override
					public void accept(User user) {
						user.setStatus(foundation.getActive());
					}
				});
				foundation.getOrganizations().forEach(new Consumer<Organization>() {
					@Override
					public void accept(Organization organization) {
						organization.setActive(foundation.getActive());
					}
				});
				organizationRepository.saveAll(foundation.getOrganizations());
				userRepository.saveAll(foundation.getUsers());
				foundationRepository.save(foundation);
				log.debug("organization {} changed", id);
				return ResponseModel.done();
			}
		}).orElseThrow(NotFoundException::new);
	}

	public ResponseModel getAllOrganizationByFoundation(Long id) {
		return organizationService.getAllOrganizationByFoundation(id);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseModel getUsersInFoundation(Long foundationId) {
		log.debug("get users in organization {}", foundationId);
		return foundationRepository.findOneByIdAndDeletedFalse(foundationId).map(new Function<Foundation, ResponseModel>() {
			@Override
			public ResponseModel apply(Foundation foundation) {
				if (SecurityUtils.isCurrentUserInRole(UserType.SYSTEM_ADMIN.name())
						|| SecurityUtils.isCurrentUserInRole(UserType.SUPER_ADMIN.name())) {
					return ResponseModel.done(userRepository.findByFoundationAndDeletedFalse(foundation).map(UserModel::new)
							.collect(Collectors.toList()));
				} else if (SecurityUtils.isCurrentUserInRole(UserType.FOUNDATION_ADMIN.name())) {
					return ResponseModel.done(userRepository.findByFoundationAndDeletedFalse(foundation)
							.filter(new Predicate<User>() {
								@Override
								public boolean test(User user) {
									return user.getType() == UserType.ADMIN || user.getType() == UserType.USER;
								}
							})
							.map(UserModel::new).collect(Collectors.toList()));
				}
				throw new NotPermittedException();
			}
		}).orElseThrow(NotFoundException::new);
	}
}
