package com.edumento.b2b.services;

import static org.springframework.data.jpa.domain.Specification.where;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.mappers.GroupsMappers;
import com.edumento.b2b.model.group.GroupCreateModel;
import com.edumento.b2b.model.group.GroupModel;
import com.edumento.b2b.repo.FoundationRepository;
import com.edumento.b2b.repo.GroupsRepository;
import com.edumento.b2b.repo.OrganizationRepository;
import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.constants.Code;
import com.edumento.core.constants.Gender;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.ExistException;
import com.edumento.core.exception.InvalidException;
import com.edumento.core.exception.MintException;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.IdModel;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.ToggleStatusModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.PermissionCheck;
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.Space;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.services.SpaceService;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.model.user.UserModel;
import com.edumento.user.repo.UserRepository;

/** Created by ahmad on 3/7/16. */
@Service
public class GroupService {
	private final Logger log = LoggerFactory.getLogger(GroupService.class);

	private final GroupsRepository groupRepository;

	private final UserRepository userRepository;

	private final FoundationRepository foundationRepository;

	private final OrganizationRepository organizationRepository;

	private final JoinedRepository joinedRepository;

	private final SpaceService spaceService;

	@Autowired
	public GroupService(GroupsRepository groupRepository, UserRepository userRepository,
			FoundationRepository foundationRepository, OrganizationRepository organizationRepository,
			JoinedRepository joinedRepository, SpaceService spaceService) {
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.foundationRepository = foundationRepository;
		this.organizationRepository = organizationRepository;
		this.joinedRepository = joinedRepository;
		this.spaceService = spaceService;
	}

	@Deprecated
	@Transactional
	@Auditable(EntityAction.GROUP_CREATE)
	@PreAuthorize("hasAuthority('GROUP_CREATE') AND hasAuthority('ADMIN')")
	public ResponseModel create(GroupCreateModel groupCreateModel, Long organizationId) {
		log.debug("create group {} in organization {}", groupCreateModel.getName(), organizationId);

		return organizationRepository.findOneByIdAndDeletedFalse(organizationId).map(organization -> {
			if (groupRepository.findOneByNameAndDeletedFalse(
					String.format("%s@%s", groupCreateModel.getName(), organization.getOrgId())).isPresent()) {
				log.debug("group {} already exist in organization {}", groupCreateModel.getName(), organizationId);
				throw new ExistException("name");
			}
			Groups groups = new Groups();
			groups.setName(String.format("%s@%s", groupCreateModel.getName(), organization.getOrgId()));
			if (groupCreateModel.getGender() != null) {
				groupCreateModel.getTags().add(0, groupCreateModel.getGender().name());
			}
			if (groupCreateModel.getTags() != null) {
				groups.setTags(String.join(",", groupCreateModel.getTags()));
			}
			if (groupCreateModel.getCanAccess() != null) {
				groups.setCanAccess(String.join(",", groupCreateModel.getCanAccess()));
			}
			groups.setOrganization(organization);
			groups.setFoundation(organization.getFoundation());
			groupRepository.save(groups);
			log.debug("group created");
			return ResponseModel.done(new IdModel(groups.getId()));
		}).orElseThrow(() -> new NotFoundException("organization"));
	}

	@Transactional
	@Auditable(EntityAction.GROUP_CREATE)
	@PreAuthorize("hasAuthority('GROUP_CREATE') AND hasAuthority('ADMIN')")
	public ResponseModel create(GroupCreateModel groupCreateModel) {
		log.debug("create group {}", groupCreateModel.getName());
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			PermissionCheck.checkUserForFoundationAndOrgOperation(user, groupCreateModel.getOrganizationId(),
					groupCreateModel.getFoundationId());
			Foundation foundation = groupCreateModel.getFoundationId() != null
					? foundationRepository.findById(groupCreateModel.getFoundationId()).orElse(user.getFoundation())
					: user.getFoundation();

			Organization organization = groupCreateModel.getOrganizationId() != null ? organizationRepository
					.findById(groupCreateModel.getOrganizationId()).orElse(user.getOrganization())
					: user.getOrganization();

			if (foundation == null && organization == null) {
				throw new InvalidException("error.groups.valid.orgfound");
			}

			String groupName = organization != null
					? String.format("%s@%s", groupCreateModel.getName(), organization.getOrgId())
					: String.format("%s@%s", groupCreateModel.getName(), foundation.getCode());

			if (groupRepository.findOneByNameAndDeletedFalse(groupName).isPresent()) {
				log.warn("group {} already exist", groupCreateModel.getName());
				throw new ExistException("error.groups.name.exists");
			}
			Groups groups = new Groups();
			groups.setName(groupName);
			if (groupCreateModel.getGender() != null) {
				groupCreateModel.getTags().add(0, groupCreateModel.getGender().name());
			}
			groups.setTags(String.join(",", groupCreateModel.getTags()));
			groups.setCanAccess(String.join(",", groupCreateModel.getCanAccess()));
			groups.setOrganization(organization);
			groups.setFoundation(foundation == null ? organization.getFoundation() : foundation);
			groupRepository.save(groups);
			return ResponseModel.done(new IdModel(groups.getId()));
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('GROUP_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getGroups(PageRequest page, Long foundationId, Long organizationId, String filter,
			boolean all) {
		log.debug("get groups");
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			log.debug("get Users");
			Specification<Groups> byFoundation = null;
			Specification<Groups> byOrganization = null;
			Specification<Groups> name = null;

			if (foundationId != null) {
				Optional<Foundation> foundationIns = foundationRepository.findOneByIdAndDeletedFalse(foundationId);
				if (foundationIns.isPresent()) {
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.equal(root.get("foundation"), foundationIns.get());

				} else {
					log.warn("foundation {} not found", foundationId);
					throw new NotFoundException("error.foundation.notfound");
				}
			}

			if (organizationId != null) {
				Optional<Organization> org = organizationRepository.findOneByIdAndDeletedFalse(organizationId);
				if (org.isPresent()) {
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> root.get("organization").in(org.get());
				} else {
					log.warn("organization {} not found", organizationId);
					throw new NotFoundException("error.organization.notfound");
				}
			} else {
				byOrganization = all ? null : (root, cq, cb) -> cb.isNull(root.get("organization"));
			}

			if (filter != null) {
				name = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(
						criteriaBuilder.lower(root.get("name").as(String.class)), "%" + filter.toLowerCase() + "%");
			}
			switch (user.getType()) {
				case SUPER_ADMIN:
				case SYSTEM_ADMIN:
					break;
				case ADMIN:
					byOrganization = (root, criteriaQuery, criteriaBuilder) -> root.get("organization")
							.in(user.getOrganization());
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> root.get("foundation")
							.in(user.getFoundation());

					break;
				case FOUNDATION_ADMIN:
					byFoundation = (root, criteriaQuery, criteriaBuilder) -> root.get("foundation")
							.in(user.getFoundation());
					log.debug("foundation ADMIN COLLECTED");
					break;
				default:
					log.warn("user {} not permitted", SecurityUtils.getCurrentUserLogin());
					throw new NotPermittedException();
			}

			Page<GroupModel> groupModels = groupRepository.findAll(where(byOrganization).and(byFoundation).and(name)
					.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), false)),
					page).map(this::getGroupModel);
			return PageResponseModel.done(groupModels.getContent(), groupModels.getTotalPages(),
					groupModels.getNumber(), groupModels.getTotalElements());
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('GROUP_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getGroup(Long id) {
		log.debug("get group by id {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			Groups groups = groupRepository.findById(id).orElseThrow(NotFoundException::new);
			return ResponseModel.done(getGroupModel(groups));
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('GROUP_ASSIGN_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getUsers(Long id) {
		log.debug("Get users in group {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			Groups groups = groupRepository.findById(id).orElseThrow(NotFoundException::new);
			List<UserInfoModel> userInfoModels = groups.getUsers().stream().map(UserModel::new)
					.collect(Collectors.toList());
			return ResponseModel.done(userInfoModels);
		}).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.GROUP_DELETE)
	@PreAuthorize("hasAuthority('GROUP_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel delete(Long id) {
		log.debug("Delete Group {}", id);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> groupRepository.findById(id).map(groups -> {
					if (!groups.getUsers().isEmpty()) {
						removeUserFromGroup(groups.getUsers().stream().map(User::getId).collect(Collectors.toList()),
								id);
					}
					groupRepository.deleteById(id);
					log.debug("group {} deleted", id);
					return ResponseModel.done();
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.GROUP_DELETE)
	@PreAuthorize("hasAuthority('GROUP_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel delete(List<Long> ids) {
		if (ids != null && !ids.isEmpty()) {
			for (Long id : ids) {
				delete(id);
			}
		}
		return ResponseModel.done();
	}

	@Transactional
	@Auditable(EntityAction.GROUP_UPDATE)
	@PreAuthorize("hasAuthority('GROUP_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel update(Long id, GroupCreateModel groupCreateModel) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> groupRepository.findById(id).map(groups -> {
					groupCreateModel.setName(groupCreateModel.getName().split("@")[0]);
					String groupName = null;
					if (groups.getOrganization() != null) {
						groupName = String.format("%s@%s", groupCreateModel.getName(),
								groups.getOrganization().getOrgId());
					} else {
						groupName = String.format("%s@%s", groupCreateModel.getName(),
								groups.getFoundation().getCode());
					}

					if (!groups.getName().equalsIgnoreCase(groupName)
							&& groupRepository.findOneByNameAndDeletedFalse(groupName).isPresent()) {
						throw new ExistException("name");
					}

					groups.setName(groupName);
					if (groupCreateModel.getGender() != null) {
						groupCreateModel.getTags().add(0, groupCreateModel.getGender().name());
					}
					if (groupCreateModel.getTags() != null) {
						groups.setTags(String.join(",", groupCreateModel.getTags()));
					}
					if (groupCreateModel.getCanAccess() != null) {
						groups.setCanAccess(String.join(",", groupCreateModel.getCanAccess()));
					}
					groupRepository.save(groups);
					return ResponseModel.done(new IdModel(groups.getId()));
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.USER_UPDATE)
	@PreAuthorize("hasAuthority('USER_UPDATE') AND hasAuthority('ADMIN')")
	public ResponseModel toggleGroupStatus(ToggleStatusModel toggleStatusModel) {
		log.debug("Toggle Group Status : {}", toggleStatusModel);

		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> groupRepository.findById(toggleStatusModel.getId()).map(groups -> {
					Set<User> users = groups.getUsers();
					if (users != null) {
						users.forEach(u -> u.setStatus(toggleStatusModel.getStatus()));
						userRepository.saveAll(users);
						log.debug("group status updated");
					}
					return ResponseModel.done();
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.GROUP_UPDATE)
	@PreAuthorize("hasAuthority('GROUP_ASSIGN_CREATE') AND hasAuthority('ADMIN')")
	public ResponseModel assignUserToGroup(List<Long> usersId, Long groupId) {
		log.debug("assign user {} to group {}", usersId, groupId);
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> assginToGroup(usersId, groupId)).orElseThrow(NotPermittedException::new);
	}

	public ResponseModel assginToGroup(List<Long> usersId, Long groupId) {
		return groupRepository.findById(groupId).map(groups -> {
			Set<User> users = new HashSet<>();
			userRepository.findAllById(usersId).forEach(user -> users.add(user));
			groups.getUsers().addAll(users);
			groupRepository.save(groups);
			log.debug("user {} assigned to group {}", usersId, groupId);
			Set<Space> spaces = joinedRepository.findByGroupNameAndDeletedFalse(String.valueOf(groups.getId()))
					.map(Joined::getSpace).collect(Collectors.toSet());
			if (spaces.isEmpty()) {
				spaces = joinedRepository.findByGroupNameAndDeletedFalse(String.valueOf(groups.getName()))
						.map(Joined::getSpace).collect(Collectors.toSet());
			}

			Set<Joined> joineds = spaces.stream().flatMap(space -> users.stream().map(u -> {
				Joined joined = joinedRepository.findOneBySpaceIdAndUserIdAndDeletedFalse(space.getId(), u.getId())
						.orElseGet(() -> new Joined(u, space));
				joined.setGroupName(groups.getId().toString());
				joined.setSpaceRole(SpaceRole.COLLABORATOR);
				return joined;
			})).collect(Collectors.toSet());
			if (!joineds.isEmpty()) {
				joinedRepository.saveAll(joineds);
			}
			log.trace("reshare group");
			return ResponseModel.done();
		}).orElseThrow(NotFoundException::new);
	}

	@Transactional
	@Auditable(EntityAction.GROUP_UPDATE)
	@PreAuthorize("hasAuthority('GROUP_ASSIGN_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel removeUserFromGroup(List<Long> usersId, Long groupId) {
		return removefromGroup(usersId, groupId);
	}

	public ResponseModel removefromGroup(List<Long> usersId, Long groupId) {
		log.debug("remove user {} from group {}", usersId, groupId);
		if (usersId.isEmpty()) {
			throw new MintException(Code.INVALID);
		}
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(user -> groupRepository.findById(groupId).map(groups -> {
					var users = StreamSupport.stream(userRepository.findAllById(usersId).spliterator(), false)
							.collect(Collectors.toList());
					if (users != null && !users.isEmpty()) {
						groups.getUsers().removeAll(users);
						groupRepository.save(groups);
						Set<Joined> joineds = joinedRepository.findByGroupNameAndDeletedFalse(groups.getId().toString())
								.filter(joined -> users.contains(joined.getUser())).collect(Collectors.toSet());
						if (joineds.isEmpty()) {
							joineds = joinedRepository.findByGroupNameAndDeletedFalse(groups.getName())
									.filter(joined -> users.contains(joined.getUser())).collect(Collectors.toSet());
						}
						if (!joineds.isEmpty()) {
							joinedRepository.deleteAll(joineds);
						}
					}
					log.debug("user {} removed from group {}", usersId, groupId);
					return ResponseModel.done();
				}).orElseThrow(NotFoundException::new)).orElseThrow(NotPermittedException::new);
	}

	@Transactional
	@Auditable(EntityAction.GROUP_UPDATE)
	@PreAuthorize("hasAuthority('GROUP_ASSIGN_CREATE')AND hasAuthority('GROUP_ASSIGN_DELETE') AND hasAuthority('ADMIN')")
	public ResponseModel transferUserToGroup(List<Long> userId, Long groupIdFrom, Long groupIdTo) {
		log.debug("transfer user {} from group {} to group {}", userId, groupIdFrom, groupIdTo);
		assignUserToGroup(userId, groupIdTo);
		return removeUserFromGroup(userId, groupIdFrom);
	}

	@Transactional
	@PreAuthorize("hasAuthority('GROUP_ASSIGN_READ') AND hasAuthority('ADMIN')")
	public ResponseModel getSpacesByGroupId(Long id) {
		log.debug("get space is group {}", id);
		return userRepository
				.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> groupRepository
						.findById(id).map(spaceService::getSpaceByGroupName).orElseThrow(NotPermittedException::new))
				.orElseThrow(NotPermittedException::new);
	}

	public GroupModel getGroupModel(Groups groups) {
		log.debug("get group model");
		GroupModel groupModel = GroupsMappers.INSTANCE.groupsToGroupModel(groups);
		if (groups.getTags() != null && !groups.getTags().isEmpty()) {
			groupModel.getTags().addAll(Arrays.asList(groups.getTags().split(",")));
			if (groupModel.getTags().get(0).equalsIgnoreCase(Gender.MALE.name())
					|| groupModel.getTags().get(0).equalsIgnoreCase(Gender.FEMALE.name())) {
				groupModel.setGender(Gender.valueOf(groupModel.getTags().remove(0)));
			}
		}
		if (groups.getCanAccess() != null && !groups.getCanAccess().isEmpty()) {
			groupModel.getCanAccess().addAll(Arrays.asList(groups.getCanAccess().split(",")));
		}
		groupModel.setSpaceCount(joinedRepository.countByDistinctSpaceIdAndGroupName(groups.getId().toString()));
		groupModel.setUserCount(groups.getUsers().size());
		log.debug("group Model got:", groupModel);
		return groupModel;
	}
}
