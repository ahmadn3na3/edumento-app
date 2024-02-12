package com.edumento.notification.handlers.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
@Component
public class AnnotationRelplyHandler extends AbstractHandler {

  @Autowired
  public AnnotationRelplyHandler(
      UserRepository userRepository,
      AmqNotifier amqNotifier,
      MailService mailService,
      ObjectMapper objectMapper) {
    super(userRepository, amqNotifier, mailService, objectMapper);
    // TODO Auto-generated constructor stub
  }
}
