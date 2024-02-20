package com.edumento.b2b.domain;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.category.domain.Category;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** Created by ayman on 02/06/16. */
@Entity
@Table(name = "foundation")
@DynamicInsert
@DynamicUpdate
public class Foundation extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String name;

	/** @deprecated use {@link FoundationPackage} numberOfOrganizations */
	@Deprecated
	@Column
	private int numberOfOrganizations = 1;

	/** @deprecated use {@link FoundationPackage} numberOfUsers */
	@Column
	@Deprecated
	private Integer numberOfUsers = 1;

	@Column(nullable = false, length = 6)
	private String code;

	@Column
	private Boolean active = Boolean.TRUE;

	@OneToMany(mappedBy = "foundation")
	private List<Organization> organizations;

	@OneToMany(mappedBy = "foundation")
	private List<User> users;

	@OneToMany(mappedBy = "foundation")
	private List<Category> categories;

	@OneToMany(mappedBy = "foundation")
	private List<Role> roles;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	/** @deprecated will be module */
	@Deprecated
	@Column
	private Boolean messageEnabled = Boolean.FALSE;

	/** @deprecated will be module */
	@Deprecated
	@Column
	private Boolean marketEnabled = Boolean.FALSE;

	/** @deprecated will be module */
	@Deprecated
	@Column
	private Boolean timeLockEnabled = Boolean.FALSE;

	@Column
	private Boolean genderSensitivity = Boolean.FALSE;

	@ManyToOne
	@JoinColumn(name = "foundation_package_id", foreignKey = @ForeignKey(name = "FK_FOUNDATION_PACKAGE"))
	private FoundationPackage foundationPackage;

	@OneToMany(mappedBy = "foundation")
	private List<TimeLock> timeLocks;

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

	@Deprecated
	public int getNumberOfOrganizations() {
		return numberOfOrganizations;
	}

	@Deprecated
	public void setNumberOfOrganizations(int numberOfOrganizations) {
		this.numberOfOrganizations = numberOfOrganizations;
	}

	public List<Organization> getOrganizations() {
		return organizations;
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Deprecated
	public Integer getNumberOfUsers() {
		return numberOfUsers;
	}

	@Deprecated
	public void setNumberOfUsers(Integer numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Deprecated
	public Boolean getMessageEnabled() {
		return messageEnabled;
	}

	@Deprecated
	public void setMessageEnabled(Boolean messageEnabled) {
		this.messageEnabled = messageEnabled;
	}

	@Deprecated
	public Boolean getMarketEnabled() {
		return marketEnabled;
	}

	@Deprecated
	public void setMarketEnabled(Boolean marketEnabled) {
		this.marketEnabled = marketEnabled;
	}

	@Deprecated
	public Boolean getTimeLockEnabled() {
		return timeLockEnabled;
	}

	@Deprecated
	public void setTimeLockEnabled(Boolean timeLockEnabled) {
		this.timeLockEnabled = timeLockEnabled;
	}

	public Boolean getGenderSensitivity() {
		return genderSensitivity;
	}

	public void setGenderSensitivity(Boolean genderSensitivity) {
		this.genderSensitivity = genderSensitivity;
	}

	public List<TimeLock> getTimeLocks() {
		return timeLocks;
	}

	public void setTimeLocks(List<TimeLock> timeLocks) {
		this.timeLocks = timeLocks;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public FoundationPackage getFoundationPackage() {
		return foundationPackage;
	}

	public void setFoundationPackage(FoundationPackage foundationPackage) {
		this.foundationPackage = foundationPackage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		var that = (Foundation) o;

		if (!Objects.equals(id, that.id) || !name.equals(that.name)) {
			return false;
		}
		return code.equals(that.code);
	}

	@Override
	public int hashCode() {
		var result = id != null ? id.hashCode() : 0;
		result = 31 * result + name.hashCode();
		return 31 * result + code.hashCode();
	}
}
