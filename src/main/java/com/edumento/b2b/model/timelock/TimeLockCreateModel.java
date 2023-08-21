package com.edumento.b2b.model.timelock;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class TimeLockCreateModel {
  @NotNull(message = "error.timelock.name")
  private String name;

  @NotNull(message = "error.timelock.fromDate")
  private ZonedDateTime fromDate;

  @NotNull(message = "error.timelock.toDate")
  private ZonedDateTime toDate;

  @NotEmpty(message = "error.timelock.days")
  private List<DayModel> dayModels = new ArrayList<>();

  @NotNull(message = "error.timelock.organizationId")
  private Long organizationId;

  private String unlockPassword;

  private List<Long> groups = new ArrayList<>();
  private List<Long> roles = new ArrayList<>();
  private List<Long> users = new ArrayList<>();

  /** @return the name */
  public String getName() {
    return name;
  }

  /** @param name the name to set */
  public void setName(String name) {
    this.name = name;
  }

  /** @return the fromDate */
  public ZonedDateTime getFromDate() {
    return fromDate;
  }

  /** @param fromDate the fromDate to set */
  public void setFromDate(ZonedDateTime fromDate) {
    this.fromDate = fromDate;
  }

  /** @return the toDate */
  public ZonedDateTime getToDate() {
    return toDate;
  }

  /** @param toDate the toDate to set */
  public void setToDate(ZonedDateTime toDate) {
    this.toDate = toDate;
  }

  /** @return the dayModels */
  public List<DayModel> getDayModels() {
    return dayModels;
  }

  /** @param dayModels the dayModels to set */
  public void setDayModels(List<DayModel> dayModels) {
    this.dayModels = dayModels;
  }

  /** @return the organizationId */
  public Long getOrganizationId() {
    return organizationId;
  }

  /** @param organizationId the organizationId to set */
  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  public List<Long> getGroups() {
    return groups;
  }

  public void setGroups(List<Long> groups) {
    this.groups = groups;
  }

  public List<Long> getRoles() {
    return roles;
  }

  public void setRoles(List<Long> roles) {
    this.roles = roles;
  }

  public List<Long> getUsers() {
    return users;
  }

  public void setUsers(List<Long> users) {
    this.users = users;
  }

  public String getUnlockPassword() {
    return unlockPassword;
  }

  public void setUnlockPassword(String unlockPassword) {
    this.unlockPassword = unlockPassword;
  }

  @Override
  public String toString() {
    return "TimeLockCreateModel{" + "name='" + name + '\'' + ", fromDate=" + fromDate + ", toDate="
        + toDate + ", dayModels=" + dayModels + ", organizationId=" + organizationId + ", groups="
        + groups + ", roles=" + roles + ", users=" + users + '}';
  }
}
