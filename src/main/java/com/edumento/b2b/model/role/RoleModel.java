package com.edumento.b2b.model.role;

import java.time.ZonedDateTime;

import com.edumento.b2b.model.organization.SimpleOrganizationModel;
import com.edumento.core.model.SimpleModel;
import com.edumento.user.constant.UserType;

/** Created by ahmad on 3/29/16. */
public class RoleModel extends RoleCreateModel {

  private Long Id;

  private SimpleOrganizationModel organization;
  private SimpleModel foundation;
  private Integer numberOfUsers;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;
  private String lastModifiedBy;
  private String createBy;
  private UserType type;

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public SimpleOrganizationModel getOrganization() {
    return organization;
  }

  public void setOrganization(SimpleOrganizationModel organization) {
    this.organization = organization;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public String getCreateBy() {
    return createBy;
  }

  public void setCreateBy(String createBy) {
    this.createBy = createBy;
  }

  public Integer getNumberOfUsers() {
    return numberOfUsers;
  }

  public void setNumberOfUsers(Integer numberOfUsers) {
    this.numberOfUsers = numberOfUsers;
  }

  @Override
  public UserType getType() {
    return type;
  }

  @Override
  public void setType(UserType type) {
    this.type = type;
  }

  public SimpleModel getFoundation() {
    return foundation;
  }

  public void setFoundation(SimpleModel foundation) {
    this.foundation = foundation;
  }

  @Override
  public String toString() {
    return "RoleModel{" + "Id=" + Id + ", organization=" + organization + ", numberOfUsers="
        + numberOfUsers + ", creationDate=" + creationDate + ", lastModifiedDate="
        + lastModifiedDate + ", lastModifiedBy='" + lastModifiedBy + '\'' + ", createBy='"
        + createBy + '\'' + ", type=" + type + "} " + super.toString();
  }
}
