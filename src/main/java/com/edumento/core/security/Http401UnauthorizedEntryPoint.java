package com.edumento.core.security;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.edumento.core.constants.Code;
import com.edumento.core.model.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Returns a 401 error code (Unauthorized) to the client. */
@Component
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

  private final Logger log = LoggerFactory.getLogger(Http401UnauthorizedEntryPoint.class);

  /** Always returns a 401 error code to the client. */
  @Override
  public void commence(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2)
      throws IOException, ServletException {

    log.debug("Pre-authenticated entry point called. Rejecting access");
    log.error("AuthenticationException :: ", arg2);
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    if (arg2 instanceof InsufficientAuthenticationException) {
      // if (arg2.getCause() instanceof InvalidTokenException) {
        log.error("InvalidToken :: ", arg2);
        objectMapper.writeValue(
            response.getOutputStream(), ResponseModel.error(Code.INVALID_TOKEN, "Invalid Token"));
        return;
      // }
    }
    objectMapper.writeValue(
        response.getOutputStream(), ResponseModel.error(Code.UNKNOWN, arg2.getMessage()));
  }
}
