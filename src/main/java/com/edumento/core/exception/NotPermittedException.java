package com.edumento.core.exception;

import com.edumento.core.constants.Code;

/** Created by ahmad on 1/30/17. */
public class NotPermittedException extends MintException {

  public NotPermittedException() {
    super(Code.NOT_PERMITTED);
  }

  public NotPermittedException(String errorMessage) {
    super(Code.NOT_PERMITTED, errorMessage);
  }

  public NotPermittedException(String s, String errorMessage) {
    super(s, Code.NOT_PERMITTED, errorMessage);
  }

  public NotPermittedException(String s, Throwable throwable, String errorMessage) {
    super(s, throwable, Code.NOT_PERMITTED, errorMessage);
  }

  public NotPermittedException(Throwable throwable, String errorMessage) {
    super(throwable, Code.NOT_PERMITTED, errorMessage);
  }

  public NotPermittedException(
      String s, Throwable throwable, boolean b, boolean b1, String errorMessage) {
    super(s, throwable, b, b1, Code.NOT_PERMITTED, errorMessage);
  }
}
