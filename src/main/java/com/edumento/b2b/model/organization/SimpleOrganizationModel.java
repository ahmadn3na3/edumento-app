package com.edumento.b2b.model.organization;

import com.edumento.core.model.SimpleModel;

/** Created by ahmad on 6/12/16. */
public class SimpleOrganizationModel extends SimpleModel {
  private String organizationCode;

  private Integer organizationTimeZone;

  public SimpleOrganizationModel() {}

  public SimpleOrganizationModel(Long id, String name, String organizationCode) {
    super(id, name);
    this.organizationCode = organizationCode;
  }

  public String getOrganizationCode() {
    return organizationCode;
  }

  public void setOrganizationCode(String organizationCode) {
    this.organizationCode = organizationCode;
  }

  public Integer getOrganizationTimeZone() {
    return organizationTimeZone;
  }

  public void setOrganizationTimeZone(Integer organizationTimeZone) {
    this.organizationTimeZone = organizationTimeZone;
  }

  @Override
  public String toString() {
    return "SimpleOrganizationModel{" + "organizationCode='" + organizationCode + '\'' + "} "
        + super.toString();
  }
}
