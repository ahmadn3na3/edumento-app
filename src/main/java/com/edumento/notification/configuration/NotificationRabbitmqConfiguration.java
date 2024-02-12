package com.edumento.notification.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.edumento.core.constants.Services;

/** Created by ahmad on 5/24/17. */
@Configuration
public class NotificationRabbitmqConfiguration {
  @Autowired
  @Qualifier("messageBus")
  Exchange topicExchange;

  @Bean("notificationqueue")
  public Queue createQueue() {
    return new Queue(Services.NOTIFICATIONS.getQueue());
  }

  @Bean
  Binding createBinding(Queue notificationqueue) {
    return BindingBuilder.bind(notificationqueue)
        .to(topicExchange)
        .with(Services.NOTIFICATIONS.getRoutingKey())
        .noargs();
  }
}
