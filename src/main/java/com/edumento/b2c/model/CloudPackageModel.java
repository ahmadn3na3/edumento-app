package com.edumento.b2c.model;

import java.time.ZonedDateTime;

import com.edumento.b2c.domain.CloudPackage;
import com.edumento.core.util.DateConverter;

/** Created by ahmad on 4/20/17. */
public class CloudPackageModel extends CloudPackageCreateModel {
	private Long id;
	private ZonedDateTime creationDate;
	private ZonedDateTime lastModifiedDate;
	private Integer numberOfUsers;

	public CloudPackageModel() {
	}

	public CloudPackageModel(CloudPackage cloudPackage) {
		id = cloudPackage.getId();
		setName(cloudPackage.getName());
		setStorage(cloudPackage.getStorage());
		setPackageTimeLimit(cloudPackage.getPackageTimeLimit());
		setCommunitySizePerSpace(cloudPackage.getCommunitySizePerSpace());
		setEncryptedContent(cloudPackage.getEncryptedContent());
		setMaxCountOfRentedSpaces(cloudPackage.getMaxCountOfRentedSpaces());
		setPackageType(cloudPackage.getPackageType());
		setPrice(cloudPackage.getPrice());
		getPermission().putAll(cloudPackage.getPermission());
		creationDate = DateConverter.convertDateToZonedDateTime(cloudPackage.getCreationDate());
		lastModifiedDate = DateConverter.convertDateToZonedDateTime(cloudPackage.getLastModifiedDate());
	}

	public CloudPackageModel(Long id) {
		this.id = id;
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

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
}
