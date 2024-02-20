package com.edumento.notification.handlers.impl;

import static com.edumento.core.constants.notification.Actions.UPDATE;
import static com.edumento.core.constants.notification.EntityType.TIME_LOCK;
import static com.edumento.core.constants.notification.MessageCategory.APP;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.model.messages.BaseNotificationMessage;
import com.edumento.core.model.messages.From;
import com.edumento.core.model.messages.Target;
import com.edumento.notification.components.AmqNotifier;
import com.edumento.notification.handlers.AbstractHandler;
import com.edumento.notification.service.MailService;
import com.edumento.user.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 04/07/17. */
//TODO: TetFawar
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
  protected void onUpdate(BaseMessage notificationMessage) {
    userRepository
        .findOneByUserNameAndDeletedFalse(notificationMessage.getUserName())
        .ifPresent(
            user -> {
              Long timelockId = Long.valueOf((Integer) notificationMessage.getEntityId());
              userRepository
                  .findByTimeLockIdAndDeletedFalse(timelockId)
                  .forEach(
                      user1 -> {
                        BaseNotificationMessage baseNotificationMessage =
                            new BaseNotificationMessage(
                                ZonedDateTime.now(),
                                APP,
                                new From(user.getId(), user.getUserName()),
                                new Target(TIME_LOCK, timelockId.toString(), UPDATE));
                        amqNotifier.send(amqNotifier.saveMessage(user1, baseNotificationMessage));
                      });
            });
  }
}
