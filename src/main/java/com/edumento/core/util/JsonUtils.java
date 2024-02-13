package com.edumento.core.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.edumento.core.constants.Code;
import com.edumento.core.exception.MintException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonUtils {

  Logger logger = LoggerFactory.getLogger(JsonUtils.class);
  @Autowired private ObjectMapper objectMapper;

  public <T> T mapJsonObject(String payload, Class<T> objectClass) {
    try {
      return objectMapper.readValue(payload, objectClass);
    } catch (IOException e) {
      logger.error("error in parse", e);
      throw new MintException(e, Code.UNKNOWN, "parse error in payload data");
    }
  }
}
