package com.edumento.b2b.model.organization;

import java.time.ZonedDateTime;

import com.edumento.core.model.SimpleModel;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/2/16. */
public class OrganizationModel extends OrganizationCreateModel {
  @NotNull
  private Long id;
  private Integer currentNumberOfUsers = 0;

  private String organizationCode;

  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;

  private String createdBy;

  private String lastModifiedBy;

  private SimpleModel foundation;

  private Boolean genderSenstivity;

  private Integer organizationTimeZone;

  public Boolean getGenderSenstivity() {
    return genderSenstivity;
  }

  public void setGenderSenstivity(boolean genderSenstivity) {
    this.genderSenstivity = genderSenstivity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  @Override
  public String getOrganizationCode() {
    return organizationCode;
  }

  @Override
  public void setOrganizationCode(String organizationCode) {
    this.organizationCode = organizationCode;
  }

  public Integer getCurrentNumberOfUsers() {
    return currentNumberOfUsers;
  }

  public void setCurrentNumberOfUsers(Integer currentNumberOfUsers) {
    this.currentNumberOfUsers = currentNumberOfUsers;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public SimpleModel getFoundation() {
    return foundation;
  }

  public void setFoundation(SimpleModel foundation) {
    this.foundation = foundation;
  }

  @Override
  public Integer getOrganizationTimeZone() {
    return organizationTimeZone;
  }

  @Override
  public void setOrganizationTimeZone(Integer organizationTimeZone) {
    this.organizationTimeZone = organizationTimeZone;
  }

  @Override
  public String toString() {
    return "OrganizationModel{" + "id=" + id + ", currentNumberOfUsers=" + currentNumberOfUsers
        + ", organizationCode='" + organizationCode + '\'' + ", creationDate=" + creationDate
        + ", lastModifiedDate=" + lastModifiedDate + ", createdBy='" + createdBy + '\''
        + ", lastModifiedBy='" + lastModifiedBy + '\'' + ", foundation=" + foundation
        + ", genderSenstivity=" + genderSenstivity + ", organizationTimeZone="
        + organizationTimeZone + '}';
  }
}
