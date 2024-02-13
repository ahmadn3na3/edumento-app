package com.edumento.b2b.model.foundationpackage;

import java.time.ZonedDateTime;

import com.edumento.b2b.domain.FoundationPackage;
import com.edumento.core.util.DateConverter;

/** Created by ahmad on 4/18/17. */
public class FoundationPackageModel extends FoundationPackageCreateModel {
  private Long id;
  private ZonedDateTime creationDate;
  private ZonedDateTime modificationDate;
  private Long foundationCount;

  public FoundationPackageModel() {}

  public FoundationPackageModel(FoundationPackage foundationPackage) {
    this.id = foundationPackage.getId();
    this.setName(foundationPackage.getName());
    this.setName(foundationPackage.getName());
    this.setPackageTimeLimit(foundationPackage.getPackageTimeLimit());
    this.setStorage(foundationPackage.getStorage());
    this.setBroadcastMessages(foundationPackage.getBroadcastMessages());
    this.setIntegrationWithSIS(foundationPackage.getIntegrationWithSIS());
    this.setNumberOfUsers(foundationPackage.getNumberOfUsers());
    this.setNumberOfOrganizations(foundationPackage.getNumberOfOrganizations());
    this.setCreationDate(
        DateConverter.convertDateToZonedDateTime(foundationPackage.getCreationDate()));
    this.setModificationDate(
        DateConverter.convertDateToZonedDateTime(foundationPackage.getLastModifiedDate()));

    if (!foundationPackage.getModules().isEmpty()) {
      foundationPackage.getModules().stream()
          .forEach(module -> this.getModules().add(module.getId()));
    }

    this.foundationCount = foundationPackage.getFoundation().stream()
        .filter(foundation -> !foundation.isDeleted()).count();
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

  public ZonedDateTime getModificationDate() {
    return modificationDate;
  }

  public void setModificationDate(ZonedDateTime modificationDate) {
    this.modificationDate = modificationDate;
  }

  public Long getFoundationCount() {
    return foundationCount;
  }

  public void setFoundationCount(Long foundationCount) {
    this.foundationCount = foundationCount;
  }
}
