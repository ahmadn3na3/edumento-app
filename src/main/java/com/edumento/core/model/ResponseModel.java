package com.edumento.core.model;

import java.util.Date;
import org.springframework.core.style.ToStringCreator;
import com.edumento.core.constants.Code;
import com.fasterxml.jackson.annotation.JsonIgnore;

/** Created by ahmad on 2/17/16. */
public class ResponseModel {
  @JsonIgnore
  private Code codeType = Code.SUCCESS;
  private int code = Code.SUCCESS.getCode();
  private String message = "";
  private Object data;
  @JsonIgnore
  private Object messageData;
  private Date dateTime = new Date();

  protected ResponseModel() {}

  protected ResponseModel(Object data) {
    this.data = data;
  }

  protected ResponseModel(int code, String message) {
    this.code = code;
    this.message = message;
  }

  protected ResponseModel(Code codeType) {
    this.codeType = codeType;
    code = codeType.getCode();
  }

  protected ResponseModel(int code, String message, Object data) {

    this.code = code;
    this.message = message;
    this.data = data;
  }

  protected ResponseModel(Code code, String message) {
    this(code);
    this.message = message;
  }

  public ResponseModel(Code code, Object data) {
    this(code);

    this.data = data;
  }

  public static ResponseModel done(String message) {
    return done(message, null);
  }

  public static ResponseModel done(String message, Object messagedata) {
    ResponseModel responseModel = new ResponseModel();
    responseModel.setMessage(message);
    responseModel.setMessageData(messagedata);
    return responseModel;
  }

  public static ResponseModel done() {
    return new ResponseModel();
  }

  public static ResponseModel done(Object data) {
    ResponseModel responseModel = new ResponseModel(data);
    responseModel.setMessageData(data);
    return responseModel;
  }

  public static ResponseModel done(Object data, Object messageData) {
    ResponseModel responseModel = new ResponseModel(data);
    responseModel.setMessageData(messageData);
    return responseModel;
  }

  public static ResponseModel error(Code code) {

    return new ResponseModel(code);
  }

  public static ResponseModel error(Code code, String message) {

    return new ResponseModel(code, message);
  }

  public static ResponseModel error(Code code, Object data) {

    return new ResponseModel(code, data);
  }

  public static ResponseModel error(Code code, String message, Object data) {

    return new ResponseModel(code.getCode(), message, data);
  }

  public Code getCodeType() {
    return codeType;
  }

  public void setCodeType(Code codeType) {
    this.codeType = codeType;
    this.code = codeType.getCode();
  }

  public final String getMessage() {
    return message;
  }

  public final void setMessage(String message) {
    this.message = message;
  }

  public final int getCode() {
    return code;
  }

  public final void setCode(int code) {
    this.code = code;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public Object getMessageData() {
    return messageData;
  }

  public void setMessageData(Object messageData) {
    this.messageData = messageData;
  }

  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  @Override
  public String toString() {
    return new ToStringCreator(this).append("codeType", codeType).append("code", code)
        .append("message", message).append("data", data).append("messageData", messageData)
        .append("dateTime", dateTime).toString();
  }
}
