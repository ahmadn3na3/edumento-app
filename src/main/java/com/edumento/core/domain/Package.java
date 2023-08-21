package com.edumento.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Package extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private Long storage;

  @Column
  private Long packageTimeLimit;

  @Column
  private Integer numberOfTags = 1000;

  @Column
  private Integer numberOfTagsGroup = 100;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

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

  public Integer getNumberOfTags() {
    return numberOfTags;
  }

  public void setNumberOfTags(Integer numberOfTags) {
    this.numberOfTags = numberOfTags;
  }

  public Integer getNumberOfTagsGroup() {
    return numberOfTagsGroup;
  }

  public void setNumberOfTagsGroup(Integer numberOfTagsGroup) {
    this.numberOfTagsGroup = numberOfTagsGroup;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Package aPackage = (Package) o;

    if (id != null ? !id.equals(aPackage.id) : aPackage.id != null)
      return false;
    return name.equals(aPackage.name);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + name.hashCode();
    return result;
  }
}
