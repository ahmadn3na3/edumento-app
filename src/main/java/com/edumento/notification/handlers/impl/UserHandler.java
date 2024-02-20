package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.FOLLOW;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.notification.MessageCategory;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.UserFollowMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.models.NotificationMessage;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class UserHandler extends AbstractHandler {
	private static final Logger logger = LoggerFactory.getLogger(UserHandler.class);

	@Autowired
	public UserHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper) {
		super(userRepository, amqNotifier, mailService, objectMapper);
	}

	@Override
	protected void handleNonCRUDAction(BaseMessage notificationMessage) {
		switch (notificationMessage.getEntityAction().getAction()) {
		case FOLLOW:
			onFollow(notificationMessage);
			break;
		default:
			break;
		}
	}

	private void onFollow(BaseMessage message) {
		UserFollowMessage userFollowMessage = mapJsonObject(message, UserFollowMessage.class);
		// TODO: sunday
		BaseNotificationMessage baseNotificationMessage = new BaseNotificationMessage(MessageCategory.USER,
				new From(userFollowMessage.getFollowerInfoMessage()),
				new Target(message.getEntityAction().getEntity(),
						userFollowMessage.getFollowerInfoMessage().getId().toString(),
						message.getEntityAction().getAction(), userFollowMessage.getFollowerInfoMessage().getImage()));

		NotificationMessage notificationMessage = amqNotifier.saveMessage(userFollowMessage.getUserInfoMessage(),
				baseNotificationMessage, createMessage(message), null);
		userRepository.findById(userFollowMessage.getUserInfoMessage().getId()).ifPresent(user -> {
			if (user.getNotification()) {
				if (user.getMailNotification()) {
					mailService.sendNotificationMail(notificationMessage, userFollowMessage.getUserInfoMessage(), true,
							true);
				}
			}
		});
	}

	@Override
	protected void onCreate(BaseMessage notificationMessage) {
		UserInfoMessage userInfoMessage = mapJsonObject(notificationMessage, UserInfoMessage.class);
		switch (notificationMessage.getEntityAction()) {
		case USER_REGISTER:
			mailService.sendActivationEmail(userInfoMessage);
			break;
		case USER_CREATE:
			mailService.sendCreationEmail(userInfoMessage);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onUpdate(BaseMessage notificationMessage) {
		UserInfoMessage userInfoMessage = mapJsonObject(notificationMessage, UserInfoMessage.class);
		switch (notificationMessage.getEntityAction()) {
		case USER_REACTIVATE:
			mailService.sendActivationEmail(userInfoMessage);
			break;
		case USER_FORGETPASSOWORD:
			mailService.sendPasswordResetMail(userInfoMessage);
			break;

		default:
			break;
		}
	}
}
