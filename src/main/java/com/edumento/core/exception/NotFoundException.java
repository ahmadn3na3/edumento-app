package com.edumento.core.exception;

import com.edumento.core.constants.Code;

/** Created by ahmad on 1/30/17. */
public class NotFoundException extends MintException {
  public NotFoundException() {
    super(Code.NOT_FOUND);
  }

  public NotFoundException(String errorMessage) {
    super(Code.NOT_FOUND, errorMessage);
  }

  public NotFoundException(String s, String errorMessage) {
    super(s, Code.NOT_FOUND, errorMessage);
  }

  public NotFoundException(String s, Throwable throwable, String errorMessage) {
    super(s, throwable, Code.NOT_FOUND, errorMessage);
  }

  public NotFoundException(Throwable throwable, String errorMessage) {
    super(throwable, Code.NOT_FOUND, errorMessage);
  }

  public NotFoundException(String s, Throwable throwable, boolean b, boolean b1,
      String errorMessage) {
    super(s, throwable, b, b1, Code.NOT_FOUND, errorMessage);
  }
}
