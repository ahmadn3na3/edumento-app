package com.edumento.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;

/** Created by ayman on 13/07/17. */
@Configuration
public class MessageConverter {
  @Bean
  public MappingJackson2MessageConverter mappingJackson2MessageConverter(
      ObjectMapper objectMapper) {
    MappingJackson2MessageConverter mappingJackson2MessageConverter =
        new MappingJackson2MessageConverter();
    mappingJackson2MessageConverter.setObjectMapper(objectMapper);
    return mappingJackson2MessageConverter;
  }
}
