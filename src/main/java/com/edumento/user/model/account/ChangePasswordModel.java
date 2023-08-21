package com.edumento.user.model.account;

import com.edumento.user.model.user.UserCreateModel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Created by ahmad on 3/3/16. */
public class ChangePasswordModel {
  @NotNull(message = "error.password.null")
  private String oldPassword;

  @NotNull(message = "error.password.null")
  @Size(
    min = UserCreateModel.PASSWORD_MIN_LENGTH,
    max = UserCreateModel.PASSWORD_MAX_LENGTH,
    message = "error.password.length"
  )
  private String password;

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return String.format(
        "ChangePasswordModel{oldPassword='%s', password='%s'}", oldPassword, password);
  }
}
