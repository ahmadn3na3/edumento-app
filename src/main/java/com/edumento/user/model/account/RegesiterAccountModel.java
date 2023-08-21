package com.edumento.user.model.account;



import com.edumento.user.model.user.UserCreateModel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Created by ahmad on 2/17/16. */
public class RegesiterAccountModel extends UserCreateModel {
  @NotNull(message = "error.password.null")
  @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "error.password.length")
  @NotEmpty
  private String password;

  private Long packageId;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long getPackageId() {
    return packageId;
  }

  public void setPackageId(Long packageId) {
    this.packageId = packageId;
  }

  @Override
  public String toString() {
    return "RegesiterAccountModel{" + "password='" + password + '\'' + "} " + super.toString();
  }
}
