package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.CREATE;
import static com.edumento.core.constants.notification.EntityType.DISCUSSION;
import static com.edumento.core.constants.notification.MessageCategory.USER;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.discussion.DiscussionMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.models.NotificationMessage;
import com.edumento.notification.service.MailService;
import com.edumento.notification.util.Utilities;
import com.edumento.space.domain.Joined;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class DiscussionReplyHandler extends AbstractHandler {

  private final Utilities utilities;

  @Autowired
  public DiscussionReplyHandler(
      UserRepository userRepository,
      AmqNotifier amqNotifier,
      MailService mailService,
      ObjectMapper objectMapper,
      Utilities utilities) {
    super(userRepository, amqNotifier, mailService, objectMapper);
    // TODO Auto-generated constructor stub
    this.utilities = utilities;
  }

  @Override
  protected void onCreate(BaseMessage baseMessage) {
    {
      DiscussionMessage discussionMessage = mapJsonObject(baseMessage, DiscussionMessage.class);
      logger.debug("message recived in descussion  message {} ", baseMessage.getDataModel());
      List<Joined> joinedList =
          utilities.getCommunityUserList(
              discussionMessage.getSpaceId(), discussionMessage.getFrom().getId());
      logger.debug("joined list {}", joinedList.size());
      BaseNotificationMessage baseNotificationMessage =
          new BaseNotificationMessage(
              ZonedDateTime.now(),
              USER,
              discussionMessage.getFrom(),
              new Target(DISCUSSION, discussionMessage.getId(), CREATE));
      joinedList
          .stream()
          .filter(
              joined ->
                  joined.getSpaceRole() != SpaceRole.VIEWER
                      && joined.getUser().getId() != discussionMessage.getFrom().getId())
          .forEach(
              joined -> {
                UserInfoMessage userInfoMessage = new UserInfoMessage(joined.getUser());
                logger.trace("save notification {}", userInfoMessage.getLogin());
                NotificationMessage notificationMessage = null;
                if(discussionMessage.getType()
                		== DiscussionType.INQUIRY) {
                	notificationMessage = amqNotifier.saveMessage(
                            userInfoMessage,
                            baseNotificationMessage,
                            createMessage(EntityAction.INQUERY_COMMENT_CREATE),
                            null,
                            discussionMessage.getSpaceName());

                }else {
                	notificationMessage = amqNotifier.saveMessage(
                            userInfoMessage,
                            baseNotificationMessage,
                            createMessage(baseMessage),
                            discussionMessage.getTitle(),
                            discussionMessage.getSpaceName(),
                            discussionMessage.getCategoryName());
                }
                if (joined.getNotification()
                    && joined.getUser().getNotification()
                    && !joined.getUser().isDeleted()) {
                  logger.trace("sending notification allowed {} ", userInfoMessage.getLogin());
                  amqNotifier.send(notificationMessage);
                }
                if (joined.getUser().getMailNotification()) {
                    mailService.sendNotificationMail(
                        notificationMessage, userInfoMessage, true, true);
                }
              });
    }
  }
}
