package com.edumento.b2b.services;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.b2b.domain.TimeLock;
import com.edumento.b2b.mappers.OrganizationMapper;
import com.edumento.b2b.model.organization.OrganizationCreateModel;
import com.edumento.b2b.model.organization.OrganizationModel;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.GroupsRepository;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.b2b.repo.TimeLockRepository;
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
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.space.services.SpaceService;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserModel;
import com.edumento.user.repo.UserRepository;
import com.edumento.user.services.UserAdministrationService;

/** Created by ahmad on 3/2/16. */
@Service
public class OrganizationService {
	private final Logger log = LoggerFactory.getLogger(OrganizationService.class);

	private final OrganizationRepository organizationRepository;

	private final UserRepository userRepository;

	private final UserAdministrationService userService;

	private final CategoryService categoryService;

	private final GroupsRepository groupsRepository;

	private final GroupService groupService;

	private final SpaceService spaceService;

	private final TimeLockRepository timeLockRepository;

	private final FoundationRepository foundationRepository;
	private final RoleService roleService;

	@Autowired
	public OrganizationService(GroupService groupService, FoundationRepository foundationRepository,
			OrganizationRepository organizationRepository, CategoryService categoryService,
			GroupsRepository groupsRepository, UserRepository userRepository, UserAdministrationService userService,
			SpaceService spaceService, TimeLockRepository timeLockRepository, RoleService roleService) {
		this.groupService = groupService;
		this.foundationRepository = foundationRepository;
		this.organizationRepository = organizationRepository;
		this.categoryService = categoryService;
		this.groupsRepository = groupsRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.spaceService = spaceService;
		this.timeLockRepository = timeLockRepository;
		this.roleService = roleService;
	}

	@Transactional
	@Auditable(EntityAction.ORGANIZATION_CREATE)
	@PreAuthorize("hasAuthority('ORGANIZATION_CREATE') AND hasAnyAuthority('SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	public ResponseModel create(OrganizationCreateModel createModel) {
		log.debug("create organization :", createModel);
		if (organizationRepository.findOneByNameAndDeletedFalse(createModel.getName()).isPresent()
				|| foundationRepository.findOneByNameAndDeletedFalse(createModel.getName()).isPresent()) {
			log.warn("organization {} already exits", createModel.getName());
			throw new ExistException("name");
		}

		if (organizationRepository.findOneByOrgIdAndDeletedFalse(createModel.getOrganizationCode()).isPresent()
				|| foundationRepository.findOneByCodeAndDeletedFalse(createModel.getOrganizationCode()).isPresent()) {
			log.warn("organizatio id {} already exist", createModel.getOrganizationCode());
			throw new ExistException("code");
		}
		Foundation foundation = null;
		if (createModel.getFoundationId() != null) {
			foundation = foundationRepository.findById(createModel.getFoundationId())
					.orElseThrow(NotFoundException::new);
		}

		if (organizationRepository.countByFoundationAndDeletedFalse(foundation) >= foundation.getFoundationPackage()
				.getNumberOfOrganizations()) {
			throw new MintException(Code.INVALID, "error.foundation.org.limit");
		}

		if (!createModel.getOrganizationCode().matches(GeneralConstant.CODE_PATTERN)) {
			throw new InvalidException("error.code.invaild");
		}

		var organization = new Organization();
		organization.setActive(createModel.isActive());
		organization.setName(createModel.getName());
		organization.setEndDate(foundation.getEndDate());
		organization.setStartDate(foundation.getStartDate());
		organization.setOrgId(createModel.getOrganizationCode());
		organization.setFoundation(foundation);
		organization.setMessageEnabled(foundation.getFoundationPackage().getBroadcastMessages());
		organization.setGenderSensitivity(foundation.getGenderSensitivity());
		organization.setOrganizationTimeZone(createModel.getOrganizationTimeZone());
		organizationRepository.save(organization);
		// defaultService.addDefaultToOrganization(organization);
		log.debug("organization created");
		return ResponseModel.done(organization.getId());
	}

	@Transactional
	@Auditable(EntityAction.ORGANIZATION_UPDATE)
	@PreAuthorize("hasAuthority('ORGANIZATION_UPDATE') AND hasAnyAuthority('SUPER_ADMIN','SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	public ResponseModel update(Long id, OrganizationCreateModel updateModel) {
		log.debug("update organization {} with model", id, updateModel);
		var organization = organizationRepository.findById(id).orElseThrow(NotFoundException::new);
		if (!updateModel.getName().equals(organization.getName())) {
			var tempOrganization = organizationRepository
					.findOneByNameAndDeletedFalse(updateModel.getName());
			if (tempOrganization.isPresent() && !id.equals(tempOrganization.get().getId())) {
				throw new ExistException("organization");
			}
		}

		organization.setName(updateModel.getName());
		organization.setEndDate(organization.getFoundation().getEndDate());
		organization.setStartDate(organization.getFoundation().getStartDate());
		organization.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
		organization.setActive(updateModel.isActive());
		organization.setOrganizationTimeZone(updateModel.getOrganizationTimeZone());
		organization.getUsers().forEach(new Consumer<User>() {
			@Override
			public void accept(User user) {
				user.setStatus(updateModel.isActive());
				user.setStartDate(organization.getStartDate());
				user.setEndDate(organization.getEndDate());
			}
		});
		organizationRepository.save(organization);
		log.debug("organization {} updated", updateModel.getOrganizationCode());
		// userRepository.save(organization.getUsers());

		return ResponseModel.done();
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ORGANIZATION_READ') AND hasAnyAuthority('SUPER_ADMIN','SYSTEM_ADMIN','FOUNDATION_ADMIN','ADMIN')")
	public ResponseModel getOrganization(Long id) {
		log.debug("get organization {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				if (user.getType() == UserType.ADMIN && !user.getOrganization().getId().equals(id)) {
					throw new NotPermittedException();
				}
				return organizationRepository.findOneByIdAndDeletedFalse(id)
						.map(new Function<Organization, ResponseModel>() {
							@Override
							public ResponseModel apply(Organization organization) {
								return ResponseModel.done(mapOrganizationToModel(organization));
							}
						})
						.orElseThrow(NotFoundException::new);
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@PreAuthorize("hasAuthority('ORGANIZATION_READ') AND hasAnyAuthority('SUPER_ADMIN','SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getAllOrganization(PageRequest page) {
		log.debug("get all organizations");

		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, PageResponseModel>() {
			@Override
			public PageResponseModel apply(User user) {
				return switch (user.getType()) {
				case SUPER_ADMIN, SYSTEM_ADMIN -> {
					var systemAdminPage = organizationRepository.findAll(page);
					yield PageResponseModel.done(
											systemAdminPage.getContent().stream()
													.filter(new Predicate<Organization>() {
														@Override
														public boolean test(Organization organization1) {
															return !"mint".equals(organization1.getName());
														}
													})
													.map(OrganizationService.this::mapOrganizationToModel).collect(Collectors.toList()),
											systemAdminPage.getTotalPages(), page.getPageNumber(), systemAdminPage.getContent().size());
				}
				case FOUNDATION_ADMIN -> {
					var foundationAdminPage = organizationRepository
							.findByFoundationIdAndDeletedFalse(user.getFoundation().getId(), page);
					yield PageResponseModel.done(
											foundationAdminPage.getContent().stream().map(OrganizationService.this::mapOrganizationToModel)
													.collect(Collectors.toList()),
											foundationAdminPage.getTotalPages(), page.getPageNumber(),
											foundationAdminPage.getTotalElements());
				}
				default -> {
					log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
					throw new NotPermittedException();
				}
				};
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Auditable(EntityAction.ORGANIZATION_DELETE)
	@Transactional
	@PreAuthorize("hasAuthority('ORGANIZATION_DELETE') AND hasAnyAuthority('SUPER_ADMIN','SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	public ResponseModel delete(Long id) {
		log.debug("delete organization {}", id);
		if (null != id) {
			return organizationRepository.findOneByIdAndDeletedFalse(id).map(new Function<Organization, ResponseModel>() {
				@Override
				public ResponseModel apply(Organization organization) {
					if ("MINT".equals(organization.getOrgId())) {
						log.warn("not permitted to delete MINT");
						throw new NotPermittedException();
					}
					log.debug("deleting users in organization {}", organization.getId());
					userService.deleteUserInOrganization(organization.getId());

					log.debug("deleting spaces in organization {}", organization.getId());
					spaceService.deleteSpacesInOrganization(organization);

					log.debug("deleting categories in organization {}", organization.getId());
					categoryService.deleteInOrganization(organization);

					log.debug("deleting time locks in organization {}", organization.getId());
					List<TimeLock> timeLocks = timeLockRepository.findByOrganizationAndDeletedFalse(organization)
							.collect(Collectors.toList());
					if (!timeLocks.isEmpty()) {
						timeLockRepository.deleteAll(timeLocks);
					}
					groupService.delete(organization.getGroups().stream().filter(new Predicate<Groups>() {
						@Override
						public boolean test(Groups groups) {
							return !groups.isDeleted();
						}
					})
							.map(Groups::getId).collect(Collectors.toList()));
					roleService.delete(organization.getRoles().stream().filter(new Predicate<Role>() {
						@Override
						public boolean test(Role role) {
							return !role.isDeleted();
						}
					}).map(Role::getId)
							.collect(Collectors.toList()));
					organizationRepository.delete(organization);
					log.debug("organization {} deleted", organization.getId());
					return ResponseModel.done();
				}
			}).orElseThrow(NotFoundException::new);
		}
		throw new MintException(Code.INVALID, "id");
	}

	@Auditable(EntityAction.ORGANIZATION_DELETE)
	@Transactional
	@PreAuthorize("hasAuthority('ORGANIZATION_DELETE') AND hasAnyAuthority('SUPER_ADMIN','SYSTEM_ADMIN','FOUNDATION_ADMIN')")
	public boolean delete(Set<Organization> organizationList) {
		log.debug("delete list of organizations");
		if (null != organizationList && !organizationList.isEmpty()) {
			organizationList.forEach(new Consumer<Organization>() {
				@Override
				public void accept(Organization organization) {
					log.debug("deleting users in organization {}", organization.getId());
					userService.deleteUserInOrganization(organization.getId());

					log.debug("deleting spaces in organization {}", organization.getId());
					spaceService.deleteSpacesInOrganization(organization);

					log.debug("deleting caregories in organization {}", organization.getId());
					categoryService.deleteInOrganization(organization);

					log.debug("deleting question bank in organization {}", organization.getId());

					log.debug("deleting time locks in organization {}", organization.getId());
					List<TimeLock> timeLocks = timeLockRepository.findByOrganizationAndDeletedFalse(organization)
							.collect(Collectors.toList());
					if (!timeLocks.isEmpty()) {
						timeLockRepository.deleteAll(timeLocks);
					}
					groupService.delete(organization.getGroups().stream().filter(new Predicate<Groups>() {
						@Override
						public boolean test(Groups groups) {
							return !groups.isDeleted();
						}
					})
							.map(Groups::getId).collect(Collectors.toList()));
					roleService.delete(organization.getRoles().stream().filter(new Predicate<Role>() {
						@Override
						public boolean test(Role role) {
							return !role.isDeleted();
						}
					}).map(Role::getId)
							.collect(Collectors.toList()));

					organizationRepository.delete(organization);
				}
			});
			return true;
		}
		return false;
	}

	@Deprecated
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('USER_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getUsersInOrganization(Long orgId) {
		log.debug("get users in organization {}", orgId);
		return ResponseModel.done(userRepository.findByOrganizationIdAndDeletedFalseAndOrganizationDeletedFalse(orgId)
				.map(UserModel::new).collect(Collectors.toList()));
	}

	@Deprecated
	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('GROUP_READ')  AND hasAuthority('ADMIN')")
	public ResponseModel getGroupsInOrganization(Long orgId) {
		log.debug("get Groups in organization {}", orgId);
		return ResponseModel.done(groupsRepository.findByOrganizationIdAndDeletedFalse(orgId)
				.map(groupService::getGroupModel).collect(Collectors.toList()));
	}

	@Transactional
	@Auditable(EntityAction.ORGANIZATION_UPDATE)
	@PreAuthorize("hasAuthority('ORGANIZATION_UPDATE') AND hasAnyAuthority('SYSTEM_ADMIN','FOUNDATION_ADMIN','SUPER_ADMIN')")
	public ResponseModel changeOrganizationStatus(Long id) {
		log.debug("change organization {} status", id);
		return organizationRepository.findOneByIdAndDeletedFalse(id).map(new Function<Organization, ResponseModel>() {
			@Override
			public ResponseModel apply(Organization organization) {
				if ("mint".equals(organization.getName())) {
					log.warn("Not permitted to change MINT Status");
					throw new NotPermittedException();
				}
				organization.setActive(!organization.getActive());
				organization.getUsers().forEach(new Consumer<User>() {
					@Override
					public void accept(User user) {
						user.setStatus(organization.getActive());
					}
				});
				organizationRepository.save(organization);
				log.debug("organization {} changed", id);
				return ResponseModel.done();
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ORGANIZATION_READ') AND hasAnyAuthority('SYSTEM_ADMIN','FOUNDATION_ADMIN','SUPER_ADMIN')")
	public ResponseModel getAllOrganizationByFoundation(Long id) {
		log.debug("get all organization in foundation {}", id);
		return foundationRepository.findOneByIdAndDeletedFalse(id)
				.map(new Function<Foundation, ResponseModel>() {
					@Override
					public ResponseModel apply(Foundation foundation) {
						return ResponseModel.done(organizationRepository.findByFoundationIdAndDeletedFalse(id)
								.map(OrganizationService.this::mapOrganizationToModel).collect(Collectors.toList()));
					}
				})
				.orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("foundation");
					}
				});
	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getSpacesByOrganizationId(Long id) {
		return organizationRepository.findOneByIdAndDeletedFalse(id).map(spaceService::getSpacesByOrganization)
				.orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("organization");
					}
				});
	}

	@PreAuthorize("hasAnyAuthority('ADMIN')")
	@Transactional(readOnly = true)
	public ResponseModel getCategoriesByOrganizationId(Long id) {
		return organizationRepository.findOneByIdAndDeletedFalse(id).map(categoryService::getCategoriesByOrganization)
				.orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("organization");
					}
				});
	}

	private OrganizationModel mapOrganizationToModel(Organization organization) {
		var organizationModel = OrganizationMapper.INSTANCE.organizationToOrganizationModel(organization);
		organizationModel
				.setCreationDate(ZonedDateTime.ofInstant(organization.getCreationDate().toInstant(), ZoneOffset.UTC));
		if (organization.getLastModifiedDate() != null) {
			organizationModel.setLastModifiedDate(
					ZonedDateTime.ofInstant(organization.getLastModifiedDate().toInstant(), ZoneOffset.UTC));
		}
		organizationModel.setGenderSenstivity(organization.getFoundation().getGenderSensitivity() == null ? false
				: organization.getFoundation().getGenderSensitivity());
		organizationModel.setCurrentNumberOfUsers(userRepository.countByOrganizationAndDeletedFalse(organization));
		return organizationModel;
	}
}
