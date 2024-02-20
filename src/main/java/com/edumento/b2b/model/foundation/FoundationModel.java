package com.edumento.b2b.model.foundation;

import java.time.ZonedDateTime;

/** Created by ayman on 02/06/16. */
public class FoundationModel extends FoundationCreateModel {
	private Long id;
	private int organizationsCount;
	private int usersCount;
	private ZonedDateTime creationDate;
	private ZonedDateTime lastModifiedDate;
	private int organizationsCapacity;
	private int usersCapacity;

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

	public int getOrganizationsCount() {
		return organizationsCount;
	}

	public void setOrganizationsCount(int organizationsCount) {
		this.organizationsCount = organizationsCount;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public int getOrganizationsCapacity() {
		return organizationsCapacity;
	}

	public void setOrganizationsCapacity(int numberOfOrganization) {
		organizationsCapacity = numberOfOrganization;
	}

	public int getUsersCapacity() {
		return usersCapacity;
	}

	public void setUsersCapacity(int usersCapacity) {
		this.usersCapacity = usersCapacity;
	}

	@Override
	public String toString() {
		return "FoundationModel{" + "id=" + id + ", organizationsCount=" + organizationsCount + ", creationDate="
				+ creationDate + ", lastModifiedDate=" + lastModifiedDate + "} " + super.toString();
	}
}
