package com.edumento.b2c.model;

import java.util.HashMap;
import java.util.Map;
import com.edumento.core.constants.PackageType;

/** Created by ahmad on 4/19/17. */
public class CloudPackageCreateModel {
  private String name;
  private Long storage;
  private Long packageTimeLimit;
  private Integer communitySizePerSpace;
  private Boolean encryptedContent;
  private Integer maxCountOfRentedSpaces;
  private PackageType packageType;
  private Map<String, Byte> permission = new HashMap<>();
  private Double price;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getStorage() {
    return storage;
  }

  public void setStorage(Long storage) {
    this.storage = storage;
  }

  public Long getPackageTimeLimit() {
    return packageTimeLimit;
  }

  public void setPackageTimeLimit(Long packageTimeLimit) {
    this.packageTimeLimit = packageTimeLimit;
  }

  public Integer getCommunitySizePerSpace() {
    return communitySizePerSpace;
  }

  public void setCommunitySizePerSpace(Integer communitySizePerSpace) {
    this.communitySizePerSpace = communitySizePerSpace;
  }

  public Boolean getEncryptedContent() {
    return encryptedContent;
  }

  public void setEncryptedContent(Boolean encryptedContent) {
    this.encryptedContent = encryptedContent;
  }

  public Integer getMaxCountOfRentedSpaces() {
    return maxCountOfRentedSpaces;
  }

  public void setMaxCountOfRentedSpaces(Integer maxCountOfRentedSpaces) {
    this.maxCountOfRentedSpaces = maxCountOfRentedSpaces;
  }

  public PackageType getPackageType() {
    return packageType;
  }

  public void setPackageType(PackageType packageType) {
    this.packageType = packageType;
  }

  public Map<String, Byte> getPermission() {
    return permission;
  }

  public void setPermission(Map<String, Byte> permission) {
    this.permission = permission;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
