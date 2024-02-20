package com.edumento.space.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.repo.GroupsRepository;
import com.edumento.b2b.services.GroupService;
import com.edumento.content.repos.ContentRepository;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.GeneralConstant;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.UserRelationType;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.UserFollowMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.space.domain.UserRelation;
import com.edumento.space.mappers.SpaceMapper;
import com.edumento.space.model.community.CommunityListModel;
import com.edumento.space.model.space.response.SpaceCommunityModel;
import com.edumento.space.model.space.response.SpaceUserModel;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.UserRelationRepository;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.repo.UserRepository;

@Service
public class CommunityService {
	private final Logger log = LoggerFactory.getLogger(CommunityService.class);

	private final JoinedRepository joinedRepository;

	private final UserRelationRepository userRelationRepository;

	private final ContentRepository contentRepository;

	private final UserRepository userRepository;

	private final GroupsRepository groupsRepository;

	private final GroupService groupService;

	@Autowired
	public CommunityService(JoinedRepository joinedRepository, UserRelationRepository userRelationRepository,
			UserRepository userRepository, GroupsRepository groupsRepository, GroupService groupService,
			ContentRepository contentRepository) {
		this.joinedRepository = joinedRepository;
		this.userRelationRepository = userRelationRepository;
		this.userRepository = userRepository;
		this.groupsRepository = groupsRepository;
		this.groupService = groupService;
		this.contentRepository = contentRepository;
	}

	@Transactional(readOnly = true)
	@PreAuthorize("hasAuthority('COMMUNITY_READ')")
	public ResponseModel getSpaceCommunity(Long spaceId, boolean addCurrent) {
		log.debug("get community for space {}", spaceId);

		SpaceCommunityModel communityModel = new SpaceCommunityModel();
		joinedRepository.getSpaceCommunity(spaceId).filter(joined -> (addCurrent || !joined.getUser().getId().equals(SecurityUtils.getCurrentUser().getId()))).forEach(joined -> {
					SpaceUserModel spaceUserModel = SpaceMapper.INSTANCE.userToSpaceUserModel(joined.getUser());

					spaceUserModel.setFollow(userRelationRepository.countByUserUserNameAndFollowIdAndDeletedFalse(
							SecurityUtils.getCurrentUserLogin(), joined.getUser().getId()) > 0);
					spaceUserModel.setSpaceRole(joined.getSpaceRole());
					spaceUserModel.setNumberOfAnnotation(joined.getAnnotationsCount());
					spaceUserModel.setNumberOfAddedContents(
							contentRepository.countBySpaceAndUser(joined.getSpace(), joined.getUser()));
					spaceUserModel.setNumberOfAssessments(joined.getAssessmentCount());
					spaceUserModel.setNumberOfDiscussions(joined.getDiscussionsCount());
					spaceUserModel.setNumberOfSpaceViews(joined.getSpaceViewsCount());
					spaceUserModel.setNumberOfDiscussionComments(joined.getDiscussionCommentsCount());

					String groupName = joined.getGroupName();
					if (groupName != null && groupName.matches("\\d+")) {
						Groups groups = groupsRepository.findById(Long.valueOf(groupName)).orElse(null);
						if (groups != null) {
							groupName = groups.getName();
						}
						Set<SpaceUserModel> spaceUserModels = communityModel.getGroups().getOrDefault(groupName,
								new HashSet<>());
						spaceUserModels.add(spaceUserModel);
						communityModel.getGroups().put(groupName, spaceUserModels);
					} else {
						communityModel.getJoinedUsers().add(spaceUserModel);
					}
				});
		return ResponseModel.done(communityModel);
	}

	@Transactional
	public ResponseModel toggleBlock(Long userId) {
		log.debug("Toggle Block User ", userId);
		return userRelationRepository
				.findOneByUserUserNameAndFollowIdAndDeletedFalse(SecurityUtils.getCurrentUserLogin(), userId)
				.map(blockedUser -> {
					if (blockedUser.getRelationType() == UserRelationType.BLOCKED) {
						userRelationRepository.delete(blockedUser);
					} else {
						blockedUser.setRelationType(UserRelationType.BLOCKED);
						userRelationRepository.save(blockedUser);
					}
					log.debug("User {} unblocked", blockedUser.getId());
					return ResponseModel.done();
				}).orElseGet(() -> {
					UserRelation blockedUser = new UserRelation();
					User currentUser = userRepository
							.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).get();
					User blockingUser = userRepository.findById(userId).orElseThrow(NotFoundException::new);

					blockedUser.setFollow(blockingUser);
					blockedUser.setUser(currentUser);
					blockedUser.setRelationType(UserRelationType.BLOCKED);
					userRelationRepository.save(blockedUser);
					log.debug("User {} Blocked", blockedUser.getId());
					return ResponseModel.done();
				});
	}

	@Transactional
	@Message(entityAction = EntityAction.USER_FOLLOW, services = Services.NOTIFICATIONS)
	public ResponseModel toggleFollow(Long userId) {
		log.debug("toggle follow User {}", userId);
		return userRelationRepository
				.findOneByUserUserNameAndFollowIdAndDeletedFalse(SecurityUtils.getCurrentUserLogin(), userId)
				.map(userRelation -> {
					userRelationRepository.delete(userRelation);
					log.debug("unfollow user {}", userId);
					return ResponseModel.done();
				}).orElseGet(() -> {
					UserRelation userRelation = new UserRelation();
					User currentUser = userRepository
							.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).get();
					User followUser = userRepository.findById(userId).orElseThrow(NotFoundException::new);
					if (followUser == null) {
						log.warn("user {} Not found", userId);
						throw new NotFoundException("user");
					}
					userRelation.setFollow(followUser);
					userRelation.setUser(currentUser);
					userRelationRepository.save(userRelation);
					log.debug("follow user {} done", userId);
					return ResponseModel.done(null,
							new UserFollowMessage(new UserInfoMessage(followUser), new UserInfoMessage(currentUser)));
				});
	}

	@Transactional(readOnly = true)
	public ResponseModel listUserToShare(Long spaceId, String search, Integer page, Integer size) {
		log.debug("list user groups to share");
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).map(user -> {
			boolean isEmail = search.matches(GeneralConstant.EMAIL_PATTERN);

			final Set<UserInfoModel> userInfoModels = new HashSet<>();
			userInfoModels.add(new UserInfoModel(user));
			if (spaceId != null) {
				userInfoModels.addAll(joinedRepository.findBySpaceIdAndDeletedFalse(spaceId)
						.map(joined -> new UserInfoModel(joined.getUser())).collect(Collectors.toSet()));
			}

			CommunityListModel communityListModel = new CommunityListModel();
			if (user.getFoundation() == null) {
				if (isEmail) {
					userRepository.findOneByEmailAndDeletedFalseAndOrganizationIsNullAndFoundationIsNull(search)
							.ifPresent(user1 -> communityListModel.getUserInfoModels().add(new UserInfoModel(user1)));
				} else {
					Specification<User> startWithInUserName = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.like(criteriaBuilder.lower(root.get("userName").as(String.class)),
									"%" + search.toLowerCase() + "%");
					Specification<User> startWithInFullName = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.like(criteriaBuilder.lower(root.get("fullName").as(String.class)),
									"%" + search.toLowerCase() + "%");
					Specification<User> idNotIn = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
							.not(root.get("id")
									.in(userInfoModels.stream().map(UserInfoModel::getId).collect(Collectors.toSet())));
					Specification<User> organizationFoundation = (root, criteriaQuery,
							criteriaBuilder) -> criteriaBuilder.and(criteriaBuilder.isNull(root.get("organization")),
									criteriaBuilder.isNull(root.get("foundation")));
					Page<User> userList = userRepository.findAll(
							Specification.where(startWithInUserName).or(startWithInFullName)
									.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
											.equal(root.get("deleted"), Boolean.FALSE))
									.and(idNotIn).and(organizationFoundation)
									.and((r, q, cb) -> cb.equal(r.get("status"), Boolean.TRUE)),
						 PageRequest.of(page, size, Sort.Direction.ASC, "fullName"));

					communityListModel.getUserInfoModels().addAll(userList.getContent().stream()
							.filter(user1 -> !Arrays
									.asList(new UserType[] { UserType.SUPER_ADMIN, UserType.SYSTEM_ADMIN })
									.contains(user1.getType()))
							.map(UserInfoModel::new).collect(Collectors.toSet()));
				}
			} else {
				if (isEmail && SecurityUtils.isCurrentUserInRole("ADMIN")) {
					userRepository
							.findOneByEmailAndDeletedFalseAndOrganizationAndFoundation(search, user.getOrganization(),
									user.getFoundation())
							.ifPresent(user1 -> communityListModel.getUserInfoModels().add(new UserInfoModel(user1)));
				} else {
					Set<Long> canAccess = new HashSet<>();
					user.getGroups().stream().distinct().forEach(s -> {
						if (s.getCanAccess() != null) {
							log.debug("can access not null ", s.getCanAccess());
							canAccess.addAll(
									Arrays.stream(s.getCanAccess().split(",")).filter(string -> string.matches("\\d+"))
											.map(Long::new).collect(Collectors.toList()));
						}
						canAccess.add(s.getId());
						log.debug("can access permission added ,{}", canAccess);
					});

					// Add check for group read permission;
					if (!canAccess.isEmpty()) {
						Set<UserInfoModel> userSet;
						if (isEmail) {
							userSet = userRepository
									.findByGroupsIdInAndFoundationAndEmailAndDeletedFalse(canAccess,
											user.getFoundation(), search,
											 PageRequest.of(page, size, Sort.Direction.ASC, "fullName"))
									.getContent().stream().map(UserInfoModel::new).collect(Collectors.toSet());
						} else {
							Specification<User> startWithInUserName = (root, criteriaQuery,
									criteriaBuilder) -> criteriaBuilder.like(
											criteriaBuilder.lower(root.get("userName").as(String.class)),
											"%" + search.toLowerCase() + "%");
							Specification<User> startWithInFullName = (root, criteriaQuery,
									criteriaBuilder) -> criteriaBuilder.like(
											criteriaBuilder.lower(root.get("fullName").as(String.class)),
											"%" + search.toLowerCase() + "%");
							Specification<User> idNotIn = (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
									.not(root.get("id").in(userInfoModels.stream().map(UserInfoModel::getId)
											.collect(Collectors.toSet())));
							Specification<User> canAccessSpec = (root, criteriaQuery,
									criteriaBuilder) -> (root.join("groups").get("id").in(canAccess));
							Specification<User> organizationFoundation = (root, criteriaQuery,
									criteriaBuilder) -> criteriaBuilder.equal(root.get("foundation"),
											user.getFoundation());
							Page<User> userList = userRepository.findAll(
									Specification.where(canAccessSpec)
											.and(Specification.where(startWithInUserName).or(startWithInFullName))
											.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder
													.equal(root.get("deleted"), Boolean.FALSE))
											.and(idNotIn).and(organizationFoundation),
									PageRequest.of(page, size, Sort.Direction.ASC, "fullName"));
							userSet = userList.getContent().stream().map(UserInfoModel::new)
									.collect(Collectors.toSet());
						}
						communityListModel.getUserInfoModels().addAll(userSet);
						communityListModel.getGroupModels().addAll(groupsRepository.findAllById(canAccess).stream()
								.filter(groups -> joinedRepository.countBySpaceIdAndGroupNameAndDeletedFalse(spaceId,
										groups.getId().toString()) == 0
										&& groups.getName().toLowerCase().contains(search.toLowerCase()))
								.map(groupService::getGroupModel).collect(Collectors.toSet()));
					}
					log.debug("user groups {}", communityListModel);
				}
			}

			return ResponseModel.done(communityListModel);
		}).orElseThrow(NotPermittedException::new);
	}
}
