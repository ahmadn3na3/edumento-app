package com.edumento.notification.components;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.user.UserInfoMessage;
import com.edumento.notification.domian.Notification;
import com.edumento.notification.models.NotificationMessage;
import com.edumento.notification.repo.NotificationRepository;
import com.edumento.user.domain.User;

/** Created by ayman on 02/03/17. */
@Component
public class AmqNotifier {

	private static final Logger logger = LoggerFactory.getLogger(AmqNotifier.class);
	// private JmsTemplate jmsTemplate;
	private NotificationRepository notificationRepository;
	private final MessageSource messageSource;

	@Value("${mint.pushnotification.enable:false}")
	private boolean enablePushNotification;

	@Value("${mint.pushnotification.message-expire:0}")
	private long messageExpire;

	@Value("${mint.pushnotification.delivery-mode:0}")
	private int deliveryMode;

	@Value("${mint.pushnotification.time-to-live:0}")
	private int timeToLive;

	@Autowired
	public AmqNotifier(MessageSource messageSource) {
//		this.jmsTemplate = jmsTemplate;
//		this.jmsTemplate.setMessageConverter(mappingJackson2MessageConverter);
//		this.jmsTemplate.setDeliveryMode(deliveryMode);
//		this.jmsTemplate.setMessageTimestampEnabled(true);
		notificationRepository = notificationRepository;
		this.messageSource = messageSource;
	}

	@Deprecated
	public NotificationMessage saveMessage(User user, BaseNotificationMessage baseNotificationMessage) {
		return saveMessage(user, baseNotificationMessage, null, null);
	}

	@Deprecated
	public NotificationMessage saveMessage(User user, BaseNotificationMessage notificationMessage, String message) {
		return saveMessage(new UserInfoMessage(user), notificationMessage, message, null);
	}

	@Deprecated
	public NotificationMessage saveMessage(User user, BaseNotificationMessage notificationMessage, String message,
			Object... objects) {
		return saveMessage(new UserInfoMessage(user), notificationMessage, message, null, objects);
	}

	public NotificationMessage saveMessage(UserInfoMessage user, BaseNotificationMessage baseNotificationMessage) {
		return saveMessage(user, baseNotificationMessage, null, null);
	}

	private void saveMessageInRepo(NotificationMessage notificationMessage) {
		notificationMessage.setId(save(notificationMessage).getId());
	}

	public NotificationMessage saveMessage(UserInfoMessage user, BaseNotificationMessage notificationMessage,
			String message, String body, Object... objects) {
		var amqNotificationMessage = new NotificationMessage(notificationMessage);
		amqNotificationMessage.setUserId(user.getId());
		if (message != null && !message.isEmpty()) {
			amqNotificationMessage.setMessage(
					messageSource.getMessage(message, objects, message, Locale.forLanguageTag(user.getLang())));
		}
		amqNotificationMessage.setBody(body);
		saveMessageInRepo(amqNotificationMessage);
		return amqNotificationMessage;
	}

	public List<NotificationMessage> saveAll(List<UserInfoMessage> userInfoMessageList,
			BaseNotificationMessage baseNotificationMessage, String message, String body, Object... objects) {
		List<NotificationMessage> notificationMessages = userInfoMessageList.stream().map(new Function<UserInfoMessage, NotificationMessage>() {
			@Override
			public NotificationMessage apply(UserInfoMessage user) {
				var amqNotificationMessage = new NotificationMessage(baseNotificationMessage);
				amqNotificationMessage.setUserId(user.getId());
				if (message != null && !message.isEmpty()) {
					amqNotificationMessage.setMessage(
							messageSource.getMessage(message, objects, message, Locale.forLanguageTag(user.getLang())));
				}
				amqNotificationMessage.setBody(body);
				return amqNotificationMessage;
			}
		}).collect(Collectors.toList());
		save(notificationMessages).forEach(new Consumer<Notification>() {
			@Override
			public void accept(Notification notification) {
				notificationMessages.stream().filter(new Predicate<NotificationMessage>() {
					@Override
					public boolean test(NotificationMessage m) {
						return notification.getUserId().equals(m.getUserId());
					}
				}).findFirst()
						.ifPresent(new Consumer<NotificationMessage>() {
							@Override
							public void accept(NotificationMessage notificationMessage) {
								notificationMessage.setId(notification.getId());
							}
						});
			}
		});

		return notificationMessages;
	}

	@Async
	public void send(NotificationMessage notificationMessage) {
		if (enablePushNotification) {
//			jmsTemplate.convertAndSend(notificationMessage, message1 -> {
//				message1.setLongProperty("userId", notificationMessage.getUserId());
//				return message1;
//			});
		}
		logger.info("Message Sent : {}", notificationMessage.toString());
	}

	@Async
	public void sendAll(List<NotificationMessage> notificationMessageList) {
		if (enablePushNotification) {
			notificationMessageList.stream().forEach(new Consumer<NotificationMessage>() {
				@Override
				public void accept(NotificationMessage notificationMessage) {
					send(notificationMessage);
				}
			});
		}
	}

	private Notification save(NotificationMessage notificationMessage) {
		var notification = new Notification(notificationMessage.getUserId(), notificationMessage.getMessage(),
				notificationMessage.getBody(), notificationMessage.getDate(),
				notificationMessage.getNotificationCategory(), notificationMessage.getFrom(),
				notificationMessage.getTarget());
		notificationRepository.save(notification);
		return notification;
	}

	private Collection<Notification> save(Collection<NotificationMessage> notificationMessages) {
		List<Notification> notifications = notificationMessages.stream()
				.map(new Function<NotificationMessage, Notification>() {
					@Override
					public Notification apply(NotificationMessage notificationMessage) {
						return new Notification(notificationMessage.getUserId(),
								notificationMessage.getMessage(), notificationMessage.getBody(), notificationMessage.getDate(),
								notificationMessage.getNotificationCategory(), notificationMessage.getFrom(),
								notificationMessage.getTarget());
					}
				})
				.collect(Collectors.toList());
		notificationRepository.saveAll(notifications);
		return notifications;
	}
}
