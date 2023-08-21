package com.edumento.core.constants;

/** Created by ahmad on 5/5/16. */
public enum SortField {
  CREATION_DATE("creationDate"),
  NAME("name"),
  PUBLISH_DATE("publishDate"),
  FULL_NAME("fullName"),
  USER_NAME("userName");

  private String fieldName;

  SortField(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
