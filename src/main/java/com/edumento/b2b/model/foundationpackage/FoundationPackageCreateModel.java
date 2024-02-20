package com.edumento.b2b.model.foundationpackage;

import java.util.HashSet;
import java.util.Set;

/** Created by ahmad on 4/18/17. */
public class FoundationPackageCreateModel {
	private String name;
	private Long storage = 0L;
	private Long packageTimeLimit = 0L;
	private Boolean integrationWithSIS = Boolean.FALSE;
	private Boolean broadcastMessages = Boolean.FALSE;
	private Integer numberOfOrganizations = 1;
	private Integer numberOfUsers = 1;
	private Set<Long> modules = new HashSet<>();

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

	public Boolean getIntegrationWithSIS() {
		return integrationWithSIS;
	}

	public void setIntegrationWithSIS(Boolean integrationWithSIS) {
		this.integrationWithSIS = integrationWithSIS;
	}

	public Boolean getBroadcastMessages() {
		return broadcastMessages;
	}

	public void setBroadcastMessages(Boolean broadcastMessages) {
		this.broadcastMessages = broadcastMessages;
	}

	public Integer getNumberOfOrganizations() {
		return numberOfOrganizations;
	}

	public void setNumberOfOrganizations(Integer numberOfOrganizations) {
		this.numberOfOrganizations = numberOfOrganizations;
	}

	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public Set<Long> getModules() {
		return modules;
	}

	public void setModules(Set<Long> modules) {
		this.modules = modules;
	}
}
