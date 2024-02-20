package com.edumento.b2b.domain;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.domain.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Created by ahmad on 3/7/16. */
@Entity
@Table(name = "groups")
@DynamicInsert
@DynamicUpdate
public class Groups extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, length = 60)
	private String name;

	@Column
	private String tags = "";

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "groups_users", joinColumns = {
			@JoinColumn(name = "groups", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_GROUP_USER")) }, inverseJoinColumns = {
					@JoinColumn(name = "users", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_USER_GROUP")) })
	private Set<User> users = new HashSet<>();

	@Column(length = 3000)
	private String canAccess;

	@Column
	private String type;

	@ManyToOne
	@JoinColumn(name = "organization", foreignKey = @ForeignKey(name = "FK_GROUP_ORG"))
	private Organization organization;

	@ManyToOne
	@JoinColumn(name = "foundation", foreignKey = @ForeignKey(name = "FK_GROUP_FND"))
	private Foundation foundation;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getCanAccess() {
		return canAccess;
	}

	public void setCanAccess(String canAccess) {
		this.canAccess = canAccess;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var groups = (Groups) o;

		if (getId() != null ? !getId().equals(groups.getId()) : groups.getId() != null) {
			return false;
		}
		return getName().equals(groups.getName());
	}

	@Override
	public int hashCode() {
		var result = getId() != null ? getId().hashCode() : 0;
		return 31 * result + getName().hashCode();
	}
}
