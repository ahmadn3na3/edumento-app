package com.edumento.notification.handlers.impl;

import org.springframework.stereotype.Component;

import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class QuestionHandler extends AbstractHandler {

	public QuestionHandler(UserRepository userRepository, AmqNotifier amqNotifier, MailService mailService,
			ObjectMapper objectMapper) {
		super(userRepository, amqNotifier, mailService, objectMapper);
		// TODO Auto-generated constructor stub
	}
}
