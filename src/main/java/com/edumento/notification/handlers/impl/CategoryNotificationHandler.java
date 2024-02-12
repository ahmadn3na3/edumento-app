package com.edumento.notification.handlers.impl;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.edumento.core.constants.notification.Actions.*;
import static com.edumento.core.constants.notification.EntityType.*;

import com.edumento.core.constants.notification.MessageCategory;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.Target;
import com.edumento.core.model.messages.category.CategoryMessageInfo;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.user.constant.UserType;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class CategoryNotificationHandler extends AbstractHandler {

	@Autowired
	public CategoryNotificationHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper) {
		super(userRepository, amqNotifier, mailService, objectMapper);
	}

	@Override
	@Transactional
	protected void onCreate(BaseMessage notificationMessage) {
		logger.debug("Category create");
		CategoryMessageInfo categoryMessageInfo = mapJsonObject(notificationMessage, CategoryMessageInfo.class);
		categoryNotify(categoryMessageInfo, CREATE);
	}

	@Override
	@Transactional
	protected void onDelete(BaseMessage notificationMessage) {
		logger.debug("Category delete");
		CategoryMessageInfo categoryMessageInfo = mapJsonObject(notificationMessage, CategoryMessageInfo.class);
		categoryNotify(categoryMessageInfo, DELETE);
	}

	@Override
	@Transactional
	protected void onUpdate(BaseMessage notificationMessage) {
		logger.debug("Category update");
		CategoryMessageInfo categoryMessageInfo = mapJsonObject(notificationMessage, CategoryMessageInfo.class);
		categoryNotify(categoryMessageInfo, UPDATE);
	}

	@Transactional()
	protected void categoryNotify(CategoryMessageInfo categoryMessageInfo, int action) {

		BaseNotificationMessage baseNotificationMessage = new BaseNotificationMessage(ZonedDateTime.now(),
				MessageCategory.APP, categoryMessageInfo.getFrom(),
				new Target(CATEGORY, categoryMessageInfo.getId().toString(), action));

		List<UserInfoMessage> userInfoMessages;
		if (null != categoryMessageInfo.getOrganizationId()) {
			logger.debug("handle organization category");
			userInfoMessages = userRepository
					.findByOrganizationIdAndDeletedFalse(categoryMessageInfo.getOrganizationId())
					.filter(user -> user.getType().equals(UserType.USER)).map(UserInfoMessage::new)
					.collect(Collectors.toList());

		} else if (null != categoryMessageInfo.getFoundationId()) {
			userInfoMessages = userRepository.findByFoundationIdAndDeletedFalse(categoryMessageInfo.getFoundationId())
					.filter(user -> user.getType().equals(UserType.USER)).map(UserInfoMessage::new)
					.collect(Collectors.toList());

		} else {
			userInfoMessages = userRepository.findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse()
					.filter(user -> user.getType().equals(UserType.USER)).map(UserInfoMessage::new)
					.collect(Collectors.toList());
		}

		amqNotifier.sendAll(amqNotifier.saveAll(userInfoMessages, baseNotificationMessage, null, null));
	}
}
