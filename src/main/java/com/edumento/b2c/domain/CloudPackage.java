package com.edumento.b2c.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.constants.PackageType;
import com.edumento.core.domain.Package;
import com.edumento.user.domain.User;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;

/** Created by ahmad on 3/9/17. */
@Entity
@DynamicInsert
@DynamicUpdate
public class CloudPackage extends Package {

  @Column
  private Integer communitySizePerSpace = 50;
  @Column
  private Boolean encryptedContent;
  @Column
  private Integer maxCountOfRentedSpaces = 0;

  @Column
  @Enumerated
  private PackageType packageType = PackageType.STANDARD;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "package_permissions", joinColumns = @JoinColumn(name = "package_id"),
      foreignKey = @ForeignKey(name = "FK_PACKAGE_PERMISSION"))
  @MapKeyColumn(name = "permission_name")
  private Map<String, Byte> permission = new HashMap<>();

  @OneToMany(mappedBy = "cloudPackage")
  private Set<User> users = new HashSet<>();

  @Column
  private Double price;

  public CloudPackage() {

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

  public Map<String, Byte> getPermission() {
    return permission;
  }

  public void setPermission(Map<String, Byte> permission) {
    this.permission = permission;
  }

  public PackageType getPackageType() {
    return packageType;
  }

  public void setPackageType(PackageType packageType) {
    this.packageType = packageType;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }
}
