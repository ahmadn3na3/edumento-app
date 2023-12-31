package com.edumento.discussion.services;

import com.edumento.core.configuration.auditing.Auditable;
import com.edumento.core.configuration.notifications.Message;
import com.edumento.core.constants.CommentType;
import com.edumento.core.constants.DiscussionType;
import com.edumento.core.constants.Services;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.discussion.DiscussionMessage;
import com.edumento.core.security.SecurityUtils;
import com.edumento.core.util.DateConverter;
import com.edumento.discussion.domain.Comment;
import com.edumento.discussion.domain.Discussion;
import com.edumento.discussion.mappers.CommentMapper;
import com.edumento.discussion.model.comment.CommentViewModel;
import com.edumento.discussion.model.discussion.*;
import com.edumento.discussion.repos.CommentRepository;
import com.edumento.discussion.repos.DiscussionRepository;
import com.edumento.space.domain.Joined;
import com.edumento.space.repos.JoinedRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.constant.UserType;
import com.edumento.user.repo.UserRepository;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Created by ayman on 25/08/16. */
@Service
public class DiscussionService {
  private final Logger log = LoggerFactory.getLogger(DiscussionService.class);
  private final UserRepository userRepository;
  private final DiscussionRepository discussionRepository;
  private final SpaceRepository spaceRepository;
  private final CommentService commentService;

  private final JoinedRepository joinedRepository;
  private final CommentRepository commentRepository;

  @Autowired
  public DiscussionService(
      UserRepository userRepository,
      DiscussionRepository discussionRepository,
      SpaceRepository spaceRepository,
      CommentService commentService,
      JoinedRepository joinedRepository,
      CommentRepository commentRepository) {

    this.userRepository = userRepository;
    this.discussionRepository = discussionRepository;
    this.spaceRepository = spaceRepository;
    this.commentService = commentService;
    this.joinedRepository = joinedRepository;
    this.commentRepository = commentRepository;
  }

  @Auditable(EntityAction.DISCUSSION_CREATE)
  @Transactional
  @PreAuthorize("hasAnyAuthority('DISCUSSION_CREATE','INQUIRY_CREATE')")
  @Message(entityAction = EntityAction.DISCUSSION_CREATE, services = Services.NOTIFICATIONS)
  public ResponseModel createNewDiscussion(DiscussionCreateModel discussionCreatModel) {
    log.debug("Create new discussion with model {}", discussionCreatModel);

    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              var space =
                  spaceRepository
                      .findById(discussionCreatModel.getSpaceId())
                      .orElseThrow(NotFoundException::new);

              return joinedRepository
                  .findOneBySpaceIdAndUserIdAndDeletedFalse(space.getId(), user.getId())
                  .map(
                      joinedSpace -> {
                        if (Arrays.asList(
                                SpaceRole.OWNER,
                                SpaceRole.CO_OWNER,
                                SpaceRole.EDITOR,
                                SpaceRole.COLLABORATOR)
                            .contains(joinedSpace.getSpaceRole())) {
                          Discussion discussion = new Discussion();
                          discussion.setTitle(discussionCreatModel.getTitle());
                          discussion.setBody(discussionCreatModel.getBody());
                          discussion.setResourceUrl(discussionCreatModel.getResourceUrl());
                          discussion.setOwnerId(user.getId());
                          discussion.setSpaceId(space.getId());
                          discussion.setUserName(user.getFullName());
                          discussion.setThumbnail(user.getThumbnail());
                          discussion.setType(discussionCreatModel.getType());
                          discussion.setContentId(discussionCreatModel.getContentId());
                          discussionRepository.save(discussion);
                          log.debug(
                              "discussion created successfully with id {}", discussion.getId());
                          joinedSpace.setDiscussionsCount(
                              discussionRepository.countBySpaceIdAndTypeAndOwnerIdAndDeletedFalse(
                                  space.getId(), DiscussionType.DISCUSSION, user.getId()));
                          joinedRepository.save(joinedSpace);
                          return ResponseModel.done(
                              null,
                              new DiscussionMessage(
                                  discussion.getId(),
                                  discussion.getTitle(),
                                  space.getId(),
                                  space.getName(),
                                  space.getCategory().getName(),
                                  new From(SecurityUtils.getCurrentUser()),
                                  discussion.getResourceUrl(),
                                  discussion.getBody(),
                                  discussion.getType()));
                        } else {
                          throw new NotPermittedException();
                        }
                      })
                  .orElseThrow(NotFoundException::new);
            })
        .orElseThrow(NotPermittedException::new);
  }

  @Auditable(EntityAction.DISCUSSION_COMMENT_UPDATE)
  @Transactional
  public ResponseModel update(String id, DiscussionCreateModel discussionCreateModel) {
    return discussionRepository
        .findOneByIdAndDeletedFalse(id)
        .map(
            discussion -> {
              if (discussion.getOwnerId().equals(SecurityUtils.getCurrentUser().getId())) {
                if (discussion.getComments().stream().anyMatch(c -> !c.isDeleted())
                    && discussion.getType() != DiscussionType.INQUIRY) {
                  throw new NotPermittedException();
                } else {
                  discussion.setTitle(discussionCreateModel.getTitle());
                  discussion.setBody(discussionCreateModel.getBody());
                  discussion.setResourceUrl(discussionCreateModel.getResourceUrl());
                  discussion.setContentId(discussionCreateModel.getContentId());
                  discussionRepository.save(discussion);
                }
                return ResponseModel.done(discussion.getId());
              } else throw new NotPermittedException();
            })
        .orElseThrow(NotFoundException::new);
  }

  @Auditable(EntityAction.DISCUSSION_COMMENT_CREATE)
  @Transactional
  @Message(entityAction = EntityAction.DISCUSSION_COMMENT_CREATE, services = Services.NOTIFICATIONS)
  public ResponseModel addReply(String id, CommentCreateModel commentCreateModel) {
    log.debug("add reply to discution {} , with model {}", id, commentCreateModel);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                discussionRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        discussion ->
                            joinedRepository
                                .findOneBySpaceIdAndUserIdAndDeletedFalse(
                                    discussion.getSpaceId(), user.getId())
                                .map(
                                    joined -> {
                                      if (joined.getSpaceRole() == SpaceRole.VIEWER) {
                                        throw new NotPermittedException();
                                      }
                                      if (discussion.getType() == DiscussionType.INQUIRY) {
                                        if (!discussion.getOwnerId().equals(user.getId())
                                            && !Arrays.asList(SpaceRole.CO_OWNER, SpaceRole.OWNER)
                                                .contains(joined.getSpaceRole())) {
                                          throw new NotPermittedException();
                                        }
                                      } else if (discussion.getType() == null) {
                                        discussion.setType(DiscussionType.DISCUSSION);
                                      }
                                      Comment comment =
                                          commentService.createComment(
                                              discussion.getId(),
                                              user,
                                              commentCreateModel,
                                              CommentType.DISCUSSION,
                                              discussion.getSpaceId());
                                      discussion.getComments().add(comment);
                                      // TODO: To be removed
                                      List<Comment> unUpdateComment =
                                          discussion.getComments().stream()
                                              .filter(c -> c != null && c.getSpaceId() == null)
                                              .map(
                                                  com -> {
                                                    com.setSpaceId(discussion.getSpaceId());
                                                    com.setType(CommentType.DISCUSSION);
                                                    return com;
                                                  })
                                              .collect(Collectors.toList());
                                      commentRepository.saveAll(unUpdateComment);
                                      ///////////////////////////////////////////////
                                      discussion.setLastModifiedDate(new Date());
                                      discussion.setLastModifiedBy(user.getUserName());
                                      discussionRepository.save(discussion);
                                      joined.setDiscussionCommentsCount(
                                          commentRepository
                                              .countByUserIdAndSpaceIdAndTypeAndDeletedFalse(
                                                  user.getId(),
                                                  discussion.getSpaceId(),
                                                  CommentType.DISCUSSION));
                                      joinedRepository.save(joined);
                                      return ResponseModel.done(
                                          (Object) comment.getId(),
                                          new DiscussionMessage(
                                              discussion.getId(),
                                              discussion.getTitle(),
                                              discussion.getSpaceId(),
                                              joined.getSpace().getName(),
                                              joined.getSpace().getCategory().getName(),
                                              new From(SecurityUtils.getCurrentUser()),
                                              discussion.getType()));
                                    })
                                .orElseThrow(NotPermittedException::new))
                    .orElseThrow(() -> new NotFoundException("error.discussion.notfound")))
        .orElseThrow(NotPermittedException::new);
  }

  @Auditable(EntityAction.DISCUSSION_COMMENT_LIKE)
  @Transactional
  public ResponseModel like(String id) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                commentRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        comment ->
                            discussionRepository
                                .findOneByIdAndDeletedFalse(comment.getParentId())
                                .map(
                                    discussion ->
                                        joinedRepository
                                            .findOneBySpaceIdAndUserIdAndDeletedFalse(
                                                discussion.getSpaceId(), user.getId())
                                            .map(
                                                joined -> {
                                                  if (joined
                                                      .getSpaceRole()
                                                      .equals(SpaceRole.VIEWER)) {
                                                    throw new NotPermittedException();
                                                  }
                                                  return commentService.toggleCommentLike(id, user);
                                                })
                                            .orElseThrow(NotFoundException::new))
                                .orElseThrow(NotFoundException::new))
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  @Auditable(EntityAction.DISCUSSION_DELETE)
  @Transactional
  @PreAuthorize("hasAnyAuthority('DISCUSSION_DELETE','INQUIRY_DELETE')")
  public ResponseModel delete(String id) {
    log.debug("Delete discussion with id : {}", id);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                discussionRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        discussion ->
                            joinedRepository
                                .findOneBySpaceIdAndUserIdAndDeletedFalse(
                                    discussion.getSpaceId(), user.getId())
                                .map(
                                    joined -> {
                                      if (discussion.getOwnerId().equals(user.getId())
                                          || joined.getSpaceRole().equals(SpaceRole.OWNER)
                                          || joined.getSpaceRole().equals(SpaceRole.CO_OWNER)) {
                                        discussionRepository.delete(discussion);

                                        commentService.deleteCommentbyParent(discussion.getId());
                                        joined.setDiscussionsCount(
                                            discussionRepository
                                                .countBySpaceIdAndTypeAndOwnerIdAndDeletedFalse(
                                                    discussion.getSpaceId(),
                                                    DiscussionType.DISCUSSION,
                                                    user.getId()));
                                        joinedRepository.save(joined);
                                        log.debug("Discussion with id {} successfully deleted", id);
                                        return ResponseModel.done();
                                      } else {
                                        throw new NotPermittedException();
                                      }
                                    })
                                .orElseThrow(NotFoundException::new))
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotFoundException::new);
  }

  @Transactional
  @PreAuthorize("hasAnyAuthority('DISCUSSION_READ','INQUIRY_READ')")
  public ResponseModel getAllDiscussions(
      Long spaceId, DiscussionType type, PageRequest pageRequest) {
    log.debug("Get All discussions summary on space with id : {}", spaceId);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                spaceRepository
                    .findOneByIdAndDeletedFalse(spaceId)
                    .map(
                        space -> {
                          if (!SecurityUtils.isCurrentUserInRole(UserType.ADMIN.name())) {
                            Joined joined =
                                joinedRepository
                                    .findOneBySpaceIdAndUserIdAndDeletedFalse(spaceId, user.getId())
                                    .orElseThrow(NotPermittedException::new);
                            if (joined.getSpaceRole() == SpaceRole.VIEWER) {
                              throw new NotPermittedException();
                            }
                          }

                          Page<DiscussionSummaryModel> page =
                              discussionRepository
                                  .findBySpaceIdAndTypeAndDeletedFalseOrderByCreationDateDesc(
                                      spaceId, type, pageRequest)
                                  .map(
                                      discussion -> {
                                        DiscussionSummaryModel discussionSummaryModel =
                                            new DiscussionDetailedModel();
                                        mapDiscussionSummary(discussion, discussionSummaryModel);
                                        return discussionSummaryModel;
                                      });
                          return PageResponseModel.done(
                              page.getContent(),
                              page.getTotalPages(),
                              page.getNumber(),
                              page.getTotalElements());
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotPermittedException::new);
  }

  @Transactional
  @PreAuthorize("hasAnyAuthority('DISCUSSION_READ','INQUIRY_READ')")
  public ResponseModel get(String id) {
    log.debug("Get discussion details with id : {}", id);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                discussionRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        discussion ->
                            joinedRepository
                                .findOneBySpaceIdAndUserIdAndDeletedFalse(
                                    discussion.getSpaceId(), user.getId())
                                .map(
                                    joined -> {
                                      if (joined.getSpaceRole() != SpaceRole.VIEWER) {
                                        DiscussionDetailedModel discussionDetailedModel =
                                            new DiscussionDetailedModel();
                                        mapDiscussionSummary(discussion, discussionDetailedModel);

                                        List<Comment> comments =
                                            discussion.getComments().stream()
                                                .filter(comment -> !comment.isDeleted())
                                                .collect(Collectors.toList());

                                        List<CommentViewModel> commentViewList =
                                            CommentMapper.INSTANCE.mapComments(comments);
                                        if (null != commentViewList) {
                                          discussionDetailedModel.setComments(commentViewList);
                                        }
                                        log.debug("discussion with id {} returned", id);
                                        return ResponseModel.done(discussionDetailedModel);
                                      } else {
                                        throw new NotPermittedException();
                                      }
                                    })
                                .orElseThrow(NotFoundException::new))
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotFoundException::new);
  }

  @Transactional
  @Deprecated
  public ResponseModel getUpdates(Long spaceId, Date since) {
    log.debug("Get discussions updates details space id : {} , since {}", spaceId, since);
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user -> {
              if (spaceRepository.findOneByIdAndDeletedFalse(spaceId) != null) {
                DiscussionUpdatesResponseModel discussionUpdatesResponseModel =
                    new DiscussionUpdatesResponseModel();

                discussionUpdatesResponseModel.setDeletedDiscussions(
                    discussionRepository
                        .findBySpaceIdAndDeletedTrueAndDeletedDateAfter(spaceId, since)
                        .stream()
                        .map(
                            discussion -> {
                              DiscussionSummaryModel discussionSummaryModel =
                                  new DiscussionSummaryModel();
                              mapDiscussionSummary(discussion, discussionSummaryModel);
                              return discussionSummaryModel;
                            })
                        .collect(Collectors.toList()));
                log.debug("Updates: deleted discussion");

                discussionUpdatesResponseModel.setNewDiscussions(
                    discussionRepository
                        .findBySpaceIdAndLastModifiedDateIsNullAndDeletedFalseAndCreationDateAfter(
                            spaceId, since)
                        .stream()
                        .map(
                            discussion -> {
                              DiscussionSummaryModel discussionSummaryModel =
                                  new DiscussionSummaryModel();
                              mapDiscussionSummary(discussion, discussionSummaryModel);
                              return discussionSummaryModel;
                            })
                        .collect(Collectors.toList()));
                log.debug("Updates: new discussion");

                discussionUpdatesResponseModel.setUpdatedDiscussions(
                    discussionRepository
                        .findBySpaceIdAndDeletedFalseAndLastModifiedDateAfter(spaceId, since)
                        .stream()
                        .map(
                            discussion -> {
                              DiscussionSummaryModel discussionSummaryModel =
                                  new DiscussionSummaryModel();
                              mapDiscussionSummary(discussion, discussionSummaryModel);
                              return discussionSummaryModel;
                            })
                        .collect(Collectors.toList()));
                log.debug("Updates: updated discussion");

                return ResponseModel.done(discussionUpdatesResponseModel);
              } else {
                throw new NotFoundException("space");
              }
            })
        .orElseThrow(NotFoundException::new);
  }

  @Auditable(EntityAction.DISCUSSION_COMMENT_DELETE)
  @Transactional
  public ResponseModel deleteReply(String id) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                commentRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        comment ->
                            discussionRepository
                                .findOneByIdAndDeletedFalse(comment.getParentId())
                                .map(
                                    discussion ->
                                        joinedRepository
                                            .findOneBySpaceIdAndUserIdAndDeletedFalse(
                                                discussion.getSpaceId(), user.getId())
                                            .map(
                                                joinedSpace -> {
                                                  if (comment.getUserId().equals(user.getId())
                                                      || joinedSpace
                                                          .getSpaceRole()
                                                          .equals(SpaceRole.CO_OWNER)
                                                      || joinedSpace
                                                          .getSpaceRole()
                                                          .equals(SpaceRole.OWNER)
                                                      || discussion
                                                          .getOwnerId()
                                                          .equals(user.getId())) {
                                                    discussion.getComments().remove(comment);
                                                    commentService.deleteComment(id);
                                                    discussionRepository.save(discussion);
                                                    joinedSpace.setDiscussionCommentsCount(
                                                        commentRepository
                                                            .countByUserIdAndSpaceIdAndTypeAndDeletedFalse(
                                                                user.getId(),
                                                                discussion.getSpaceId(),
                                                                CommentType.DISCUSSION));
                                                    joinedRepository.save(joinedSpace);
                                                    return ResponseModel.done();
                                                  } else {
                                                    throw new NotPermittedException();
                                                  }
                                                })
                                            .orElseThrow(NotFoundException::new))
                                .orElseThrow(NotFoundException::new))
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotFoundException::new);
  }

  @Auditable(EntityAction.DISCUSSION_COMMENT_UPDATE)
  @Transactional
  public ResponseModel editReply(String id, String body) {
    return userRepository
        .findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
        .map(
            user ->
                commentRepository
                    .findOneByIdAndDeletedFalse(id)
                    .map(
                        comment -> {
                          if (comment.getUserId().equals(user.getId())) {
                            return commentService.updateComment(id, body);
                          } else {
                            throw new NotPermittedException();
                          }
                        })
                    .orElseThrow(NotFoundException::new))
        .orElseThrow(NotFoundException::new);
  }

  private void mapDiscussionSummary(
      Discussion discussion, DiscussionSummaryModel discussionSummaryModel) {
    log.debug("Mapping Discussion {}", discussion);
    Integer commentSize = 0;
    discussionSummaryModel.setId(discussion.getId());
    discussionSummaryModel.setTitle(discussion.getTitle());
    discussionSummaryModel.setBody(discussion.getBody());
    discussionSummaryModel.setResourceUrl(discussion.getResourceUrl());
    discussionSummaryModel.setContentId(discussion.getContentId());
    discussionSummaryModel.setType(discussion.getType());
    if (discussion.getComments() != null && !discussion.getComments().isEmpty()) {
      for (Comment comment : discussion.getComments()) {
        if (comment != null && !comment.isDeleted()) {
          commentSize++;
        }
      }
    }

    discussionSummaryModel.setCommentsCounter(commentSize);
    discussionSummaryModel.setCreationDate(
        DateConverter.convertDateToZonedDateTime(discussion.getCreationDate()));
    userRepository
        .findById(discussion.getOwnerId())
        .ifPresent(
            user -> {
              discussionSummaryModel.setOwnerName(user.getFullName());
              discussionSummaryModel.setOwnerThumb(user.getThumbnail());
              discussionSummaryModel.setOwnerId(discussion.getOwnerId());
            });

    discussionSummaryModel.setSpaceId(discussion.getSpaceId());

    log.debug("mapped Model {}", discussionSummaryModel);
  }
}
