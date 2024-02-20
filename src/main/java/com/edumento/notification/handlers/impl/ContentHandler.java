package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.CREATE;
import static com.edumento.core.constants.notification.Actions.UPDATE;

import java.time.ZonedDateTime;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.notification.EntityType;
import com.edumento.core.constants.notification.MessageCategory;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.content.ContentInfoMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.notification.util.Utilities;
import com.edumento.space.domain.Joined;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class ContentHandler extends AbstractHandler {

	private final Utilities utilities;

	@Value("${enable-edit-content-email:false}")
	private boolean enableEditContentEmail;

	@Autowired
	public ContentHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper, Utilities utilities) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		this.utilities = utilities;
	}

	@Override
	protected void onCreate(BaseMessage baseMessage) {

		notify(baseMessage, CREATE, true, true, true);
	}

	@Override
	protected void onUpdate(BaseMessage baseMessage) {
		notify(baseMessage, UPDATE, true, true, enableEditContentEmail);
	}

	@Override
	protected void onDelete(BaseMessage notificationMessage) {
		super.onDelete(notificationMessage);
	}

	private void notify(BaseMessage baseMessage, int action, boolean withImage, boolean useTagImage,
			boolean enablesendemail) {
		var contentInfoMessage = mapJsonObject(baseMessage, ContentInfoMessage.class);
		if (!ContentType.WORKSHEET.equals(contentInfoMessage.getContentType())) {
			var joinedList = utilities.getCommunityUserList(contentInfoMessage.getSpaceId(),
					contentInfoMessage.getFrom().getId());
			joinedList.forEach(new Consumer<Joined>() {
				@Override
				public void accept(Joined joined) {
					var userInfoMessage = new UserInfoMessage(joined.getUser());
					var notificationMessage = amqNotifier.saveMessage(userInfoMessage,
							new BaseNotificationMessage(ZonedDateTime.now(), MessageCategory.USER,
									contentInfoMessage.getFrom(),
									new Target(EntityType.CONTENT, contentInfoMessage.getId().toString(), action)),
							createMessage(baseMessage), null, contentInfoMessage.getName(),
							contentInfoMessage.getSpaceName(), contentInfoMessage.getCategoryName());

					if (joined.getNotification() && joined.getUser().getNotification() && !joined.getUser().isDeleted()) {
						amqNotifier.send(notificationMessage);
					}
					if (enablesendemail && Boolean.TRUE.equals(joined.getUser().getMailNotification())) {
						mailService.sendNotificationMail(notificationMessage, userInfoMessage, withImage, useTagImage);
					}
				}
			});
		}
	}
}
