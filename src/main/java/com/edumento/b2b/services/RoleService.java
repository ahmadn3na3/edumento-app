package com.edumento.b2b.services;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Role;
import com.edumento.b2b.mappers.RoleMapper;
import com.edumento.b2b.model.role.AssignRoleModel;
import com.edumento.b2b.model.role.RoleByModel;
import com.edumento.b2b.model.role.RoleCreateModel;
import com.edumento.b2b.model.role.RoleModel;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.b2b.repo.RoleRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.PermissionCheck;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.repo.UserRepository;
import com.edumento.user.repo.specifications.RoleSpecifications;

/** Created by ahmad on 3/28/16. */
@Service
public class RoleService {
	private final Logger log = LoggerFactory.getLogger(RoleService.class);

	private final RoleRepository roleRepository;

	private final UserRepository userRepository;

	private final OrganizationRepository organizationRepository;

	private final FoundationRepository foundationRepository;

	public RoleService(RoleRepository roleRepository, UserRepository userRepository,
			OrganizationRepository organizationRepository, FoundationRepository foundationRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
		this.organizationRepository = organizationRepository;
		this.foundationRepository = foundationRepository;
	}

	@Transactional
	@Auditable(EntityAction.ROLE_CREATE)
	@PreAuthorize("hasAuthority('ROLE_CREATE') AND hasAuthority('ADMIN')")
	// TODO: Separate Methods into 3 methods with permission check
	public ResponseModel createRole(RoleCreateModel roleModel) {
		log.debug("create role : {}", roleModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				PermissionCheck.checkUserForFoundationAndOrgOperation(user, roleModel.getOrganizationId(),
						roleModel.getFoundationId());
				if (roleModel.getOrganizationId() != null) {
					var organization = organizationRepository.findById(roleModel.getOrganizationId())
							.orElseThrow(NotFoundException::new);
					if (!organization.getActive().booleanValue()) {
						throw new InvalidException("error.organization.active");
					}
					if (roleRepository.findOneByNameAndOrganizationAndDeletedFalse(roleModel.getName(), organization)
							.isPresent()) {
						throw new ExistException("name");
					}
					var role = new Role();
					role.setName(roleModel.getName());
					role.setType(roleModel.getType());
					role.getPermission().putAll(roleModel.getPermission());
					role.setOrganization(organization);
					role.setFoundation(organization.getFoundation());
					roleRepository.save(role);
					return ResponseModel.done();

				} else if (roleModel.getFoundationId() != null) {
					var foundation = foundationRepository.findById(roleModel.getFoundationId())
							.orElseThrow(NotFoundException::new);
					if (roleRepository.findOneByNameAndFoundationAndDeletedFalse(roleModel.getName(), foundation)
							.isPresent()) {
						throw new ExistException("name");
					}

					var role = new Role();
					role.setName(roleModel.getName());
					role.setType(roleModel.getType());
					role.getPermission().putAll(roleModel.getPermission());
					role.setFoundation(foundation);
					roleRepository.save(role);
					return ResponseModel.done();
				}
				throw new InvalidException("error.role.create.parent");
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.ROLE_UPDATE)
	@PreAuthorize("hasAuthority('ROLE_UPDATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.ROLE_UPDATE, services = Services.NOTIFICATIONS)
	public ResponseModel updateRole(Long id, RoleCreateModel roleModel) {
		log.debug("updating role {} with data {}", id, roleModel);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var role = roleRepository.findById(id).orElseThrow(NotFoundException::new);

				PermissionCheck.checkUserForFoundationAndOrgOperation(user,
						role.getOrganization() == null ? null : role.getOrganization().getId(),
						role.getFoundation().getId());

				if (!Objects.equals(role.getName(), roleModel.getName()) && roleRepository
						.findOneByNameAndFoundationAndDeletedFalse(roleModel.getName(), role.getFoundation()).isPresent()) {
					log.warn("role {} already exist", roleModel.getName());
					throw new ExistException("name");
				}

				role.setName(roleModel.getName());
				role.setType(roleModel.getType());
				role.getPermission().clear();
				role.getPermission().putAll(roleModel.getPermission());
				roleRepository.save(role);

				log.debug("role {} updated", id);
				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getRoles() {
		log.debug("get roles");
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				switch (user.getType()) {
				case SUPER_ADMIN:
				case SYSTEM_ADMIN:
					if ("admin".equalsIgnoreCase(user.getUserName())) {
						return ResponseModel.done(roleRepository.findAll().stream()
								.filter(new Predicate<Role>() {
									@Override
									public boolean test(Role role) {
										return !"superadminrole".equalsIgnoreCase(role.getName());
									}
								}).map(RoleService.this::getRoleModel)
								.collect(Collectors.toSet()));
					}
					return ResponseModel.done(roleRepository.findAll().stream().filter(
							new Predicate<Role>() {
								@Override
								public boolean test(Role role) {
									return !"superadminrole".equalsIgnoreCase(role.getName()) && role.getType() != user.getType();
								}
							})
							.map(RoleService.this::getRoleModel).collect(Collectors.toSet()));
				case FOUNDATION_ADMIN:
					return ResponseModel.done(roleRepository.findByFoundationAndDeletedFalse(user.getFoundation())
							.filter(new Predicate<Role>() {
								@Override
								public boolean test(Role role) {
									return role.getType() == UserType.ADMIN || role.getType() == UserType.USER;
								}
							})
							.map(RoleService.this::getRoleModel).collect(Collectors.toSet()));
				case ADMIN:
					return ResponseModel.done(roleRepository.findByOrganizationAndDeletedFalse(user.getOrganization())
							.filter(new Predicate<Role>() {
								@Override
								public boolean test(Role role) {
									return role.getType() == UserType.USER;
								}
							}).map(RoleService.this::getRoleModel)
							.collect(Collectors.toSet()));
				default:
					return roleRepository.findByUsersIdAndDeletedFalse(user.getId()).map(
							new Function<List<Role>, ResponseModel>() {
								@Override
								public ResponseModel apply(List<Role> roles) {
									return ResponseModel.done(roles.stream().map(RoleService.this::getRoleModel).collect(Collectors.toSet()));
								}
							})
							.orElse(ResponseModel.done(Collections.emptySet()));
				}
			}
		}).orElseThrow(NotPermittedException::new);
	}

	private RoleModel getRoleModel(Role role) {
		log.debug("Map role {} to role model", role.getId());

		var roleModel = RoleMapper.INSTANCE.roleToRoleModel(role);
		roleModel.setCreationDate(ZonedDateTime.ofInstant(role.getCreationDate().toInstant(), ZoneOffset.UTC));
		if (role.getLastModifiedDate() != null) {
			roleModel.setLastModifiedDate(
					ZonedDateTime.ofInstant(role.getLastModifiedDate().toInstant(), ZoneOffset.UTC));
		}
		roleModel.getPermission().putAll(role.getPermission());
		roleModel.setNumberOfUsers(userRepository.countByRolesIdInAndDeletedFalse(Collections.singleton(role.getId())));
		return roleModel;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getRole(Long Id) {
		log.debug("get role {}", Id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				return switch (user.getType()) {
				case SUPER_ADMIN, SYSTEM_ADMIN -> roleRepository.findOneByIdAndDeletedFalse(Id).map(new Function<Role, ResponseModel>() {
					@Override
					public ResponseModel apply(Role role) {
						return ResponseModel.done(getRoleModel(role));
					}
				})
										.orElseThrow(NotFoundException::new);
				case FOUNDATION_ADMIN -> roleRepository.findOneByFoundationAndIdAndDeletedFalse(user.getFoundation(), Id)
										.map(new Function<Role, ResponseModel>() {
											@Override
											public ResponseModel apply(Role role) {
												return ResponseModel.done(getRoleModel(role));
											}
										}).orElseThrow(NotFoundException::new);
				case ADMIN -> roleRepository
										.findOneByOrganizationInAndIdAndDeletedFalse(Collections.singletonList(user.getOrganization()),
												Id)
										.map(new Function<Role, ResponseModel>() {
											@Override
											public ResponseModel apply(Role role) {
												return ResponseModel.done(getRoleModel(role));
											}
										}).orElseThrow(NotFoundException::new);
				default -> {
					log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
					throw new NotPermittedException();
				}
				};
			}
		}).orElseThrow(NotFoundException::new);
	}

	@Deprecated
	@Transactional
	@Auditable(EntityAction.ROLE_UPDATE)
	@PreAuthorize("hasAuthority('ROLE_ASSIGN_CREATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.ROLE_ASSIGN, services = Services.NOTIFICATIONS, withModel = true, indexOfModel = 0)
	public ResponseModel assignRole(AssignRoleModel assignRoleModel) {
		log.debug("assign role {} to user {} ", assignRoleModel.getRoleId(), assignRoleModel.getUserId());
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var userToAssign = userRepository.findById(assignRoleModel.getUserId())
						.orElseThrow(new Supplier<NotFoundException>() {
							@Override
							public NotFoundException get() {
								return new NotFoundException("user");
							}
						});
				var role = roleRepository.findById(assignRoleModel.getRoleId())
						.orElseThrow(new Supplier<NotFoundException>() {
							@Override
							public NotFoundException get() {
								return new NotFoundException("role");
							}
						});
				switch (userToAssign.getType()) {
				case FOUNDATION_ADMIN:
					if (user.getType() != UserType.SYSTEM_ADMIN && user.getType() != UserType.SUPER_ADMIN) {
						log.warn("user {} not permitted", user.getId());
						throw new NotPermittedException();
					}

					if (!Objects.equals(userToAssign.getFoundation().getId(), role.getFoundation().getId())
							|| role.getType() != UserType.FOUNDATION_ADMIN) {
						log.warn(
								"invalid use organization not the same ot role organization and role type not foundation admin");
						throw new MintException(Code.INVALID, "role");
					}
					break;
				case ADMIN:
					if (!UserType.SUPER_ADMIN.equals(user.getType()) && !UserType.SYSTEM_ADMIN.equals(user.getType())
							&& !UserType.FOUNDATION_ADMIN.equals(user.getType())) {
						log.warn("user {} not permitted", user.getId());
						throw new NotPermittedException();
					}
					if (!Objects.equals(userToAssign.getOrganization().getId(), role.getOrganization().getId())
							|| role.getType() != UserType.ADMIN) {
						log.warn(
								"invalid use organization not the same ot role organization and role type not foundation admin");
						throw new MintException(Code.INVALID, "role");
					}
					break;
				}
				removeRolesFromUser(userToAssign);

				role.getUsers().add(userToAssign);
				roleRepository.save(role);
				log.debug("role {} assigned to user {} successfully", assignRoleModel.getRoleId(),
						assignRoleModel.getUserId());
				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.ROLE_UPDATE)
	@PreAuthorize("hasAuthority('ROLE_ASSIGN_CREATE') AND hasAuthority('ADMIN')")
	@Message(entityAction = EntityAction.ROLE_ASSIGN, services = Services.NOTIFICATIONS, withModel = true)
	public ResponseModel assignRole(Long roleId, List<Long> userIds) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return assignToRole(roleId, userIds);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	public ResponseModel assignToRole(Long roleId, List<Long> userIds) {
		var role = roleRepository.findById(roleId).orElseThrow(new Supplier<NotFoundException>() {
			@Override
			public NotFoundException get() {
				return new NotFoundException("role");
			}
		});

		if (userIds == null || userIds.isEmpty()) {
			log.debug("invalid users , NULL Or Empty");
			throw new MintException(Code.INVALID);
		}

		userRepository.findByIdInAndOrganizationAndTypeAndDeletedFalse(userIds, role.getOrganization(), role.getType())
				.forEach(new Consumer<User>() {
					@Override
					public void accept(User user1) {
						removeRolesFromUser(user1);
						role.getUsers().add(user1);
					}
				});

		roleRepository.save(role);
		log.debug("role {} assigned to user {} successfully", roleId, userIds);
		return ResponseModel.done();
	}

	@Transactional
	@Auditable(EntityAction.ROLE_UPDATE)
	@PreAuthorize("hasAuthority('ROLE_ASSIGN_DELETE') AND hasAuthority('ADMIN')")
	@Message(services = Services.NOTIFICATIONS, entityAction = EntityAction.ROLE_UNASSIGN, withModel = true)
	public ResponseModel unassignRole(Long roleId, List<Long> userIds) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				var role = roleRepository.findById(roleId).orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("role");
					}
				});

				if (userIds == null || userIds.isEmpty()) {
					log.debug("invalid users , NULL Or Empty");
					throw new MintException(Code.INVALID);
				}
				if (role == null) {
					log.warn("invalid role , NULL");
					throw new MintException(Code.INVALID);
				}
				userRepository
						.findByIdInAndOrganizationAndTypeAndDeletedFalse(userIds, role.getOrganization(), role.getType())
						.forEach(new Consumer<User>() {
							@Override
							public void accept(User user1) {
								role.getUsers().remove(user1);
							}
						});

				roleRepository.save(role);
				log.debug("role {} assigned to user {} successfully", roleId, userIds);
				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	public void removeRolesFromUser(User user) {
		log.debug("remove roles from user", user.getId());
		for (Role r : user.getRoles()) {
			r.getUsers().remove(user);
			roleRepository.save(r);
			log.debug("role {} removed from user {}", r.getId(), user.getId());
		}
	}

	@Transactional
	@Auditable(EntityAction.ROLE_DELETE)
	@PreAuthorize("hasAuthority('ROLE_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel delete(Long id) {
		log.debug("deleting role {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				roleRepository.findOneByIdAndDeletedFalse(id).ifPresent(new Consumer<Role>() {
					@Override
					public void accept(Role role) {
						PermissionCheck.checkUserForFoundationAndOrgOperation(user,
								role.getOrganization() == null ? null : role.getOrganization().getId(),
								role.getFoundation().getId());
						if ("SuperAdminRole".equalsIgnoreCase(role.getName())) {
							return;
						}
						role.getUsers().clear();
						roleRepository.save(role);
						log.debug("role {} removed from all users", id);
						roleRepository.delete(role);
						log.debug("role {} deleted successfully", id);
						role.getUsers().clear();
					}
				});

				return ResponseModel.done();
			}
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.ROLE_DELETE)
	@PreAuthorize("hasAuthority('ROLE_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel delete(List<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (Long id : ids) {
				delete(id);
			}
		}
		return ResponseModel.done();
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_ASSIGN_READ')  AND hasAuthority('ADMIN')")
	public ResponseModel getUserOnRole(Long id) {
		log.debug("get all users on role {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return roleRepository.findOneByIdAndDeletedFalse(id).map(new Function<Role, ResponseModel>() {
							@Override
							public ResponseModel apply(Role role) {
								switch (user.getType()) {
								case SUPER_ADMIN:
								case SYSTEM_ADMIN:
									return ResponseModel.done(role.getUsers().stream().filter(new Predicate<User>() {
										@Override
										public boolean test(User user1) {
											return !user1.isDeleted();
										}
									})
											.map(UserInfoModel::new).collect(Collectors.toList()));
								case FOUNDATION_ADMIN:
									if (role.getFoundation() != null && role.getFoundation().equals(user.getFoundation())) {
										return ResponseModel.done(role.getUsers().stream().filter(new Predicate<User>() {
											@Override
											public boolean test(User user1) {
												return !user1.isDeleted();
											}
										})
												.map(UserInfoModel::new).collect(Collectors.toList()));
									}

								case ADMIN:
									if (role.getOrganization() != null && role.getOrganization().equals(user.getOrganization())) {
										return ResponseModel.done(role.getUsers().stream().filter(new Predicate<User>() {
											@Override
											public boolean test(User user1) {
												return !user1.isDeleted();
											}
										})
												.map(UserInfoModel::new).collect(Collectors.toList()));
									}

								default:
									log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
									throw new NotPermittedException();
								}
							}
						}).orElseThrow(NotFoundException::new);
					}
				}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('ROLE_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getRoleBy(RoleByModel roleByModel) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(new Function<User, ResponseModel>() {
			@Override
			public ResponseModel apply(User user) {
				log.debug("get role by specifications:{}", roleByModel);
				Specification<Role> byOrganization = null;
				Specification<Role> byType = null;
				Specification<Role> byFoundation = null;

				if (roleByModel.getFoundationId() == null && roleByModel.getOrganizationId() == null
						&& roleByModel.getUserType() == null) {
					log.warn("Missing parameters ,one of  organization id or user type is required");
					throw new MintException(Code.MISSING, "Parameters");
				}

				if (roleByModel.getOrganizationId() == null && roleByModel.getFoundationId() == null) {
					if (user.getType() == UserType.ADMIN) {
						byOrganization = RoleSpecifications.byOrganization(user.getOrganization());
					}
					if (user.getType() == UserType.FOUNDATION_ADMIN) {
						byFoundation = RoleSpecifications.byFoundation(user.getFoundation());
					}

				} else {
					var org = organizationRepository.findById(roleByModel.getOrganizationId()).orElse(null);
					if (org != null) {
						byOrganization = RoleSpecifications.byOrganization(org);
					}
					var foundation = foundationRepository.findById(roleByModel.getFoundationId()).orElse(null);
					if (foundation != null) {
						byFoundation = RoleSpecifications.byFoundation(foundation);
					}
				}
				if (roleByModel.getUserType() != null) {
					byType = RoleSpecifications.hasType(roleByModel.getUserType());
				} else if (UserType.FOUNDATION_ADMIN.equals(user.getType())) {
					byType = RoleSpecifications.hasType(UserType.ADMIN, UserType.USER);
				} else if (UserType.ADMIN.equals(user.getType())) {
					byType = RoleSpecifications.hasType(UserType.USER);
				}
				return ResponseModel.done(roleRepository
						.findAll(Specification.where(byOrganization).and(byFoundation).and(byType)
								.and(RoleSpecifications.notDeleted()))
						.stream().filter(new Predicate<Role>() {
							@Override
							public boolean test(Role role) {
								return !"SuperAdminRole".equalsIgnoreCase(role.getName())
										&& !user.getRoles().contains(role);
							}
						})
						.map(new Function<Role, RoleModel>() {
							@Override
							public RoleModel apply(Role role) {
								var roleModel = new RoleModel();
								RoleMapper.INSTANCE.roleToRoleModel(role);
								roleModel.setNumberOfUsers(role.getUsers().size());
								return roleModel;
							}
						}).collect(Collectors.toList()));
			}
		}).orElseThrow(NotPermittedException::new);
	}
}
