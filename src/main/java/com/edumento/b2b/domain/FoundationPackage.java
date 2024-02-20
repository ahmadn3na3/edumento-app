package com.edumento.b2b.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.domain.Package;
import com.edumento.user.domain.Module;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

/** Created by ahmad on 3/9/17. */
@Entity
@DynamicInsert
@DynamicUpdate
public class FoundationPackage extends Package {

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "package_module", joinColumns = {
			@JoinColumn(name = "package_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_MODULE_PACKAGE")) }, inverseJoinColumns = {
					@JoinColumn(name = "module_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_PACKAGE_MODULE")) })
	private Set<Module> modules = new HashSet<>();

	@Column
	private Boolean integrationWithSIS = false;

	@Column
	private Boolean broadcastMessages = false;

	@Column
	private Integer numberOfOrganizations = 1;

	@Column
	private Integer numberOfUsers = 1;

	@OneToMany(mappedBy = "foundationPackage")
	private List<Foundation> foundation;

	public Set<Module> getModules() {
		return modules;
	}

	public void setModules(Set<Module> modules) {
		this.modules = modules;
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

	public List<Foundation> getFoundation() {
		return foundation;
	}

	public void setFoundation(List<Foundation> foundation) {
		this.foundation = foundation;
	}
}
