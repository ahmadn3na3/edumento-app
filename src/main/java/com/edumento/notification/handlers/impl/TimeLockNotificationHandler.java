package com.edumento.notification.handlers.impl;

import com.edumento.core.model.messages.BaseMessage;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/** Created by ayman on 04/07/17. */
// TODO: TetFawar
@Component
public class TimeLockNotificationHandler extends AbstractHandler {

  public TimeLockNotificationHandler(
      UserRepository userRepository,
      AmqNotifier amqNotifier,
      MailService mailService,
      ObjectMapper objectMapper) {
    super(userRepository, amqNotifier, mailService, objectMapper);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void onUpdate(BaseMessage notificationMessage) {}
}
