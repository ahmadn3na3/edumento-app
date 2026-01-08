package com.edumento.content.controller.advices;

import com.edumento.core.constants.Code;
import com.edumento.core.model.ResponseModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Created by ahmad on 7/20/16. */
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
  @ExceptionHandler({IOException.class, FileNotFoundException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ResponseModel handelIOException(IOException ex) {
    return ResponseModel.error(Code.UNKNOWN, ex.toString());
  }
}
