package com.edumento.notification.handlers;

import static com.edumento.core.constants.notification.Actions.CREATE;
import static com.edumento.core.constants.notification.Actions.DELETE;
import static com.edumento.core.constants.notification.Actions.UPDATE;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.edumento.core.constants.Code;
import com.edumento.core.constants.notification.EntityAction;
import com.edumento.core.exception.MintException;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 01/03/17. */
public abstract class AbstractHandler {

	protected static final String MESSAGE_PREFIX = "mint.notification.message";
	protected static Logger logger = LoggerFactory.getLogger(AbstractHandler.class);
	protected final UserRepository userRepository;

	protected final AmqNotifier amqNotifier;

	protected final MailService mailService;

	protected final ObjectMapper objectMapper;

	public AbstractHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper) {
		super();
		this.userRepository = userRepository;
		this.amqNotifier = amqNotifier;
		this.mailService = mailService;
		this.objectMapper = objectMapper;
	}

	@Transactional
	public void handle(BaseMessage notificationMessage) {
		logger.debug("message recived:?", notificationMessage.toString());
		try {
			switch (notificationMessage.getEntityAction().getAction()) {
			case UPDATE:
				onUpdate(notificationMessage);
				break;
			case CREATE:
				onCreate(notificationMessage);
				break;
			case DELETE:
				onDelete(notificationMessage);
				break;
			default:
				handleNonCRUDAction(notificationMessage);
			}
		} catch (Exception e) {
			logger.error("error in handle", e);
		}
	}

	protected void handleNonCRUDAction(BaseMessage notificationMessage) {
	}

	protected void onCreate(BaseMessage notificationMessage) {
	}

	protected void onUpdate(BaseMessage notificationMessage) {
	}

	protected void onDelete(BaseMessage notificationMessage) {
	}

	protected <T> T mapJsonObject(BaseMessage notificationMessage, Class<T> objectClass) {
		try {
			return this.objectMapper.readValue(notificationMessage.getDataModel(), objectClass);
		} catch (IOException e) {
			logger.error("error in parse", e);
			throw new MintException(e, Code.UNKNOWN, "parse error in messagedata");
		}
	}

	protected String createMessage(BaseMessage baseMessage) {
		return createMessage(baseMessage.getEntityAction());
	}

	protected String createMessage(EntityAction baseMessageEntityAction) {
		return String.format("%s.%s", MESSAGE_PREFIX, baseMessageEntityAction.getMessageSuffix());
	}
}
