package com.edumento.notification.handlers.impl;



import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.constants.notification.Actions;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.constants.notification.EntityType;
import com.edumento.core.constants.notification.MessageCategory;
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
public class DiscussionHadler extends AbstractHandler {
	private final Utilities utilities;

	@Autowired
	public DiscussionHadler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper, Utilities utilities) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		// TODO Auto-generated constructor stub
		this.utilities = utilities;
	}

	@Override
	protected void onCreate(BaseMessage baseMessage) {
		DiscussionMessage discussionMessage = mapJsonObject(baseMessage, DiscussionMessage.class);
		logger.debug("message recived in descussion  message {} ", baseMessage.getDataModel());

		BaseNotificationMessage baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(),MessageCategory.USER,
				discussionMessage.getFrom(), new Target(EntityType.DISCUSSION , discussionMessage.getId(),Actions.CREATE));
		if (discussionMessage.getType() == DiscussionType.INQUIRY) {
			utilities.getCommunityUserList(discussionMessage.getSpaceId(), discussionMessage.getFrom().getId())
					.stream().filter(joined -> Arrays.asList(SpaceRole.CO_OWNER, SpaceRole.OWNER)
							.contains(joined.getSpaceRole()))
					.map(Joined::getUser).forEach(user -> {
						UserInfoMessage userInfoMessage = new UserInfoMessage(user);
						logger.trace("save notification {}", userInfoMessage.getLogin());
						String questionBodyLimit = "";
						if(discussionMessage.getBody() != null) {
							questionBodyLimit = discussionMessage.getBody().substring(0, Math.min(discussionMessage.getBody().length(), 150));
						}
						NotificationMessage notificationMessage = amqNotifier.saveMessage(userInfoMessage,
								baseNotificationMessage, createMessage(EntityAction.INQUERY_CREATE), null,
								discussionMessage.getSpaceName(), discussionMessage.getTitle(),
								questionBodyLimit);

						if (user.getNotification()) {
							logger.trace("sending notification allowed {} ", userInfoMessage.getLogin());
							amqNotifier.send(notificationMessage);
						}
						if (user.getNotification()) {
							mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
						}
					});
		} else {
			List<Joined> joinedList = utilities.getCommunityUserList(discussionMessage.getSpaceId(),
					discussionMessage.getFrom().getId());
			logger.debug("joined list {}", joinedList.size());
			joinedList.stream()
					.filter(joined -> joined.getSpaceRole() != SpaceRole.VIEWER
							&& !joined.getUser().getId().equals(discussionMessage.getFrom().getId()))
					.forEach(joined -> {
						UserInfoMessage userInfoMessage = new UserInfoMessage(joined.getUser());
						logger.trace("save notification {}", userInfoMessage.getLogin());
						NotificationMessage notificationMessage = amqNotifier.saveMessage(userInfoMessage,
								baseNotificationMessage, createMessage(baseMessage), null, discussionMessage.getTitle(),
								discussionMessage.getSpaceName(), discussionMessage.getCategoryName());

						if (joined.getNotification() && joined.getUser().getNotification()
								&& !joined.getUser().isDeleted()) {
							logger.trace("sending notification allowed {} ", userInfoMessage.getLogin());
							amqNotifier.send(notificationMessage);
						}
						if (joined.getUser().getMailNotification()) {
							mailService.sendNotificationMail(notificationMessage, userInfoMessage, true, false);
						}
					});
		}
	}
}
