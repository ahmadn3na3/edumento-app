package com.edumento.core.constants;

/** Created by ahmad on 6/12/16. */
public enum Gender {
  MALE(true),
  FEMALE(false);
  private final Boolean value;

  Gender(boolean value) {
    this.value = value;
  }

  public Boolean getValue() {
    return value;
  }
}
