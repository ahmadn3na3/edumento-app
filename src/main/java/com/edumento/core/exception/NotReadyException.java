package com.edumento.core.exception;

import com.edumento.core.constants.Code;

public class NotReadyException extends MintException {

  public NotReadyException() {
    super(Code.NOT_READY);
  }

  public NotReadyException(String errorMessage) {
    super(Code.NOT_READY, errorMessage);
  }

  public NotReadyException(String s, String errorMessage) {
    super(s, Code.NOT_READY, errorMessage);
  }

  public NotReadyException(String s, Throwable throwable, String errorMessage) {
    super(s, throwable, Code.NOT_READY, errorMessage);
  }

  public NotReadyException(Throwable throwable, String errorMessage) {
    super(throwable, Code.NOT_READY, errorMessage);
  }

  public NotReadyException(String s, Throwable throwable, boolean b, boolean b1,
      String errorMessage) {
    super(s, throwable, b, b1, Code.NOT_READY, errorMessage);
  }
}
