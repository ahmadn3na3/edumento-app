package com.edumento.core.configuration.notifications;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.edumento.core.constants.notification.Exchnages;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.messages.BaseMessage;
import com.edumento.core.security.SecurityUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ahmad on 4/13/16. */
@Aspect
public class MessageAspect {

  private final Logger logger = LoggerFactory.getLogger(MessageAspect.class);

  @Autowired
  Notifier notifier;

  @Autowired
  ObjectMapper objectMapper;

  @Value("${mint.enableNotificaion:false}")
  private boolean enableNotificaion;

  @AfterReturning(pointcut = "@annotation(message)", returning = "responseModel")
  public void notify(JoinPoint jp, Message message, ResponseModel responseModel)
      throws JsonProcessingException {
    logger.debug("Message Aspect:{}", message.toString());
    Object id = null;
    String dataModel = null;

    if (!enableNotificaion) {
      logger.debug("notification not enabled");
      return;
    }
    logger.debug("start send message");
    if (responseModel.getMessageData() != null) {
      logger.debug("writing data model ");
      dataModel = objectMapper.writeValueAsString(responseModel.getMessageData());
    }
    if (message.entityAction().getIdClass().isInstance(responseModel.getMessageData())) {
      id = responseModel.getMessageData();
    } else if (message.entityAction().getIdClass().isInstance(jp.getArgs()[message.indexOfId()])) {
      id = jp.getArgs()[message.indexOfId()];
    }
    logger.debug("id:" + id);

    if ((id != null || dataModel != null) && (responseModel.getCode() == 10)) {
      logger.debug("sending message");
      BaseMessage baseMessage = new BaseMessage(
          message.entityAction(), id, SecurityUtils.getCurrentUserLogin(), dataModel);
      Arrays.asList(message.services())
          .forEach(s -> notifier.send(Exchnages.MESSAGE_BUS, s.getRoutingKey(), baseMessage));
    }
  }
}
