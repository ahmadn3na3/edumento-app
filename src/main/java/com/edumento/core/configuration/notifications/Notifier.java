package com.edumento.core.configuration.notifications;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.Target;

/** Created by ayman on 28/02/17. */
@Component
public class Notifier {
	public static Logger logger = LoggerFactory.getLogger(Notifier.class);

	public Notifier() {
	}

	@Async
	public void send(String exchange, String routingKey, Object message) {

	}

	@Async
	public void buildMessageAndSend(String exchnage, String routingKey, long senderId, String senderName,
			int notificationCategory, int action, int type, String targetId) {

		var message = new BaseNotificationMessage();

		var from = new From();
		var target = new Target();

		from.setId(senderId);
		from.setName(senderName);

		target.setAction(action);
		target.setType(type);
		target.setId(targetId);

		message.setDate(ZonedDateTime.now());
		message.setNotificationCategory(notificationCategory);
		message.setFrom(from);
		message.setTarget(target);

		send(exchnage, routingKey, message);
	}
}
