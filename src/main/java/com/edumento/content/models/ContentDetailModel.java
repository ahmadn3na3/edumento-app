package com.edumento.content.models;

/** Created by ahmad on 7/17/16. */
public class ContentDetailModel {
  private int code = 10;
  private String message = "";
  private ContentModel data;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ContentModel getData() {
    return data;
  }

  public void setData(ContentModel data) {
    this.data = data;
  }
}
