package com.edumento.space.services;

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
import com.edumento.space.domain.Joined;
import com.edumento.space.domain.UserRelation;
import com.edumento.space.mappers.SpaceMapper;
import com.edumento.space.model.community.CommunityListModel;
import com.edumento.space.model.space.response.SpaceCommunityModel;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.UserRelationRepository;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;
import com.edumento.user.model.user.UserInfoModel;
import com.edumento.user.repo.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.HashSet;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityService {
  private final Logger log = LoggerFactory.getLogger(CommunityService.class);

  private final JoinedRepository joinedRepository;

  private final UserRelationRepository userRelationRepository;

  private final ContentRepository contentRepository;

  private final UserRepository userRepository;

  @Autowired
  public CommunityService(
      JoinedRepository joinedRepository,
      UserRelationRepository userRelationRepository,
      UserRepository userRepository,
      ContentRepository contentRepository) {
    this.joinedRepository = joinedRepository;
    this.userRelationRepository = userRelationRepository;
    this.userRepository = userRepository;
    this.contentRepository = contentRepository;
  }

  @Transactional(readOnly = true)
  @PreAuthorize("hasAuthority('COMMUNITY_READ')")
  public ResponseModel getSpaceCommunity(Long spaceId, boolean addCurrent) {
    log.debug("get community for space {}", spaceId);

    var communityModel = new SpaceCommunityModel();
    joinedRepository
        .getSpaceCommunity(spaceId)
        .filter(
            new Predicate<Joined>() {
              @Override
              public boolean test(Joined joined) {
                return (addCurrent
                    || !joined.getUser().getId().equals(SecurityUtils.getCurrentUser().getId()));
              }
            })
        .forEach(
            new Consumer<Joined>() {
              @Override
              public void accept(Joined joined) {
                var spaceUserModel = SpaceMapper.INSTANCE.userToSpaceUserModel(joined.getUser());

                spaceUserModel.setFollow(
                    userRelationRepository.countByUserUserNameAndFollowIdAndDeletedFalse(
                            SecurityUtils.getCurrentUserLogin(), joined.getUser().getId())
                        > 0);
                spaceUserModel.setSpaceRole(joined.getSpaceRole());
                spaceUserModel.setNumberOfAnnotation(joined.getAnnotationsCount());
                spaceUserModel.setNumberOfAddedContents(
                    contentRepository.countBySpaceAndUser(joined.getSpace(), joined.getUser()));
                spaceUserModel.setNumberOfAssessments(joined.getAssessmentCount());
                spaceUserModel.setNumberOfDiscussions(joined.getDiscussionsCount());
                spaceUserModel.setNumberOfSpaceViews(joined.getSpaceViewsCount());
                spaceUserModel.setNumberOfDiscussionComments(joined.getDiscussionCommentsCount());

                communityModel.getJoinedUsers().add(spaceUserModel);
              }
            });
    return ResponseModel.done(communityModel);
  }

  @Transactional
  public ResponseModel toggleBlock(Long userId) {
    log.debug("Toggle Block User ", userId);
    return userRelationRepository
        .findOneByUserUserNameAndFollowIdAndDeletedFalse(
            SecurityUtils.getCurrentUserLogin(), userId)
        .map(
            new Function<UserRelation, ResponseModel>() {
              @Override
              public ResponseModel apply(UserRelation blockedUser) {
                if (blockedUser.getRelationType() == UserRelationType.BLOCKED) {
                  userRelationRepository.delete(blockedUser);
                } else {
                  blockedUser.setRelationType(UserRelationType.BLOCKED);
                  userRelationRepository.save(blockedUser);
                }
                log.debug("User {} unblocked", blockedUser.getId());
                return ResponseModel.done();
              }
            })
        .orElseGet(
            new Supplier<ResponseModel>() {
              @Override
              public ResponseModel get() {
                var blockedUser = new UserRelation();
                var currentUser =
                    userRepository
                        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
                        .get();
                var blockingUser =
                    userRepository.findById(userId).orElseThrow(NotFoundException::new);

                blockedUser.setFollow(blockingUser);
                blockedUser.setUser(currentUser);
                blockedUser.setRelationType(UserRelationType.BLOCKED);
                userRelationRepository.save(blockedUser);
                log.debug("User {} Blocked", blockedUser.getId());
                return ResponseModel.done();
              }
            });
  }

  @Transactional
  @Message(entityAction = EntityAction.USER_FOLLOW, services = Services.NOTIFICATIONS)
  public ResponseModel toggleFollow(Long userId) {
    log.debug("toggle follow User {}", userId);
    return userRelationRepository
        .findOneByUserUserNameAndFollowIdAndDeletedFalse(
            SecurityUtils.getCurrentUserLogin(), userId)
        .map(
            new Function<UserRelation, ResponseModel>() {
              @Override
              public ResponseModel apply(UserRelation userRelation) {
                userRelationRepository.delete(userRelation);
                log.debug("unfollow user {}", userId);
                return ResponseModel.done();
              }
            })
        .orElseGet(
            new Supplier<ResponseModel>() {
              @Override
              public ResponseModel get() {
                var userRelation = new UserRelation();
                var currentUser =
                    userRepository
                        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
                        .get();
                var followUser =
                    userRepository.findById(userId).orElseThrow(NotFoundException::new);
                if (followUser == null) {
                  log.warn("user {} Not found", userId);
                  throw new NotFoundException("user");
                }
                userRelation.setFollow(followUser);
                userRelation.setUser(currentUser);
                userRelationRepository.save(userRelation);
                log.debug("follow user {} done", userId);
                return ResponseModel.done(
                    null,
                    new UserFollowMessage(
                        new UserInfoMessage(followUser), new UserInfoMessage(currentUser)));
              }
            });
  }

  @Transactional(readOnly = true)
  public ResponseModel listUserToShare(Long spaceId, String search, Integer page, Integer size) {
    log.debug("list user groups to share");
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            new Function<User, ResponseModel>() {
              @Override
              public ResponseModel apply(User user) {
                var isEmail = search.matches(GeneralConstant.EMAIL_PATTERN);

                final Set<UserInfoModel> userInfoModels = new HashSet<>();
                userInfoModels.add(new UserInfoModel(user));
                if (spaceId != null) {
                  userInfoModels.addAll(
                      joinedRepository
                          .findBySpaceIdAndDeletedFalse(spaceId)
                          .map(
                              new Function<Joined, UserInfoModel>() {
                                @Override
                                public UserInfoModel apply(Joined joined) {
                                  return new UserInfoModel(joined.getUser());
                                }
                              })
                          .collect(Collectors.toSet()));
                }

                var communityListModel = new CommunityListModel();
                if (isEmail) {
                  userRepository
                      .findOneByEmailAndDeletedFalse(search)
                      .ifPresent(
                          new Consumer<User>() {
                            @Override
                            public void accept(User user1) {
                              communityListModel.getUserInfoModels().add(new UserInfoModel(user1));
                            }
                          });
                } else {
                  Specification<User> startWithInUserName =
                      new Specification<User>() {
                        @Override
                        @Nullable
                        public jakarta.persistence.criteria.Predicate toPredicate(
                            Root<User> root,
                            CriteriaQuery<?> criteriaQuery,
                            CriteriaBuilder criteriaBuilder) {
                          return criteriaBuilder.like(
                              criteriaBuilder.lower(root.get("userName").as(String.class)),
                              "%" + search.toLowerCase() + "%");
                        }
                      };
                  Specification<User> startWithInFullName =
                      new Specification<User>() {
                        @Override
                        @Nullable
                        public jakarta.persistence.criteria.Predicate toPredicate(
                            Root<User> root,
                            CriteriaQuery<?> criteriaQuery,
                            CriteriaBuilder criteriaBuilder) {
                          return criteriaBuilder.like(
                              criteriaBuilder.lower(root.get("fullName").as(String.class)),
                              "%" + search.toLowerCase() + "%");
                        }
                      };
                  Specification<User> idNotIn =
                      new Specification<User>() {
                        @Override
                        @Nullable
                        public jakarta.persistence.criteria.Predicate toPredicate(
                            Root<User> root,
                            CriteriaQuery<?> criteriaQuery,
                            CriteriaBuilder criteriaBuilder) {
                          return criteriaBuilder.not(
                              root.get("id")
                                  .in(
                                      userInfoModels.stream()
                                          .map(UserInfoModel::getId)
                                          .collect(Collectors.toSet())));
                        }
                      };

                  var userList =
                      userRepository.findAll(
                          Specification.where(startWithInUserName)
                              .or(startWithInFullName)
                              .and(
                                  new Specification<User>() {
                                    @Override
                                    @Nullable
                                    public jakarta.persistence.criteria.Predicate toPredicate(
                                        Root<User> root,
                                        CriteriaQuery<?> criteriaQuery,
                                        CriteriaBuilder criteriaBuilder) {
                                      return criteriaBuilder.equal(
                                          root.get("deleted"), Boolean.FALSE);
                                    }
                                  })
                              .and(idNotIn)
                              .and(
                                  new Specification<User>() {
                                    @Override
                                    @Nullable
                                    public jakarta.persistence.criteria.Predicate toPredicate(
                                        Root<User> r, CriteriaQuery<?> q, CriteriaBuilder cb) {
                                      return cb.equal(r.get("status"), Boolean.TRUE);
                                    }
                                  }),
                          PageRequest.of(page, size, Sort.Direction.ASC, "fullName"));

                  communityListModel
                      .getUserInfoModels()
                      .addAll(
                          userList.getContent().stream()
                              .filter(
                                  new Predicate<User>() {
                                    @Override
                                    public boolean test(User user1) {
                                      return !Arrays.asList(
                                              new UserType[] {
                                                UserType.SUPER_ADMIN, UserType.SYSTEM_ADMIN
                                              })
                                          .contains(user1.getType());
                                    }
                                  })
                              .map(UserInfoModel::new)
                              .collect(Collectors.toSet()));
                }

                return ResponseModel.done(communityListModel);
              }
            })
        .orElseThrow(NotPermittedException::new);
  }
}
