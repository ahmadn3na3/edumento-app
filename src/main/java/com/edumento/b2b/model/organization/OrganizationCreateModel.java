package com.edumento.b2b.model.organization;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/2/16. */
public class OrganizationCreateModel {

  @NotNull(message = "error.organization.name.null")
  private String name;

  private String organizationCode;

  private Long foundationId;

  private Integer organizationTimeZone = 0;

  private boolean active = false;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public String getOrganizationCode() {
    return organizationCode;
  }

  public void setOrganizationCode(String organizationCode) {
    this.organizationCode = organizationCode;
  }

  public Long getFoundationId() {
    return foundationId;
  }

  public void setFoundationId(Long foundationId) {
    this.foundationId = foundationId;
  }

  public Integer getOrganizationTimeZone() {
    return organizationTimeZone;
  }

  public void setOrganizationTimeZone(Integer organizationTimeZone) {
    this.organizationTimeZone = organizationTimeZone;
  }

  @Override
  public String toString() {
    return "OrganizationCreateModel{" + "name='" + name + '\'' + ", organizationCode='"
        + organizationCode + '\'' + ", foundationId=" + foundationId + ", organizationTimeZone="
        + organizationTimeZone + ", active=" + active + '}';
  }
}
