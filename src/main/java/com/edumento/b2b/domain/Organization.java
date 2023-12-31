package com.edumento.b2b.domain;

import java.util.Date;
import java.util.List;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** Created by ahmad on 2/29/16. */
@Entity
@DynamicInsert
@DynamicUpdate
public class Organization extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column private Integer numberOfUsers = 1;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date startDate;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date endDate;

  @Column private Boolean active = Boolean.FALSE;

  @Column(nullable = false, length = 6)
  private String orgId;

  @Column private Boolean messageEnabled = Boolean.FALSE;
  @Column private Boolean marketEnabled = Boolean.FALSE;
  @Column private Boolean timeLockEnabled = Boolean.FALSE;
  @Column private Boolean genderSensitivity = Boolean.FALSE;

  @Column private Integer organizationTimeZone = 0;

  @OneToMany(mappedBy = "organization")
  private List<User> users;

  @OneToMany(mappedBy = "organization")
  private List<Category> categories;

  @OneToMany(mappedBy = "organization")
  private List<Groups> groups;

  @OneToMany(mappedBy = "organization")
  private List<Role> roles;

  @OneToMany(mappedBy = "organization")
  private List<TimeLock> timeLocks;

  @ManyToOne
  @JoinColumn(name = "foundation", foreignKey = @ForeignKey(name = "FK_FOUNDATION"))
  private Foundation foundation;

  public Organization() {}

  public Integer getNumberOfUsers() {
    return numberOfUsers;
  }

  public void setNumberOfUsers(Integer numberOfUsers) {
    this.numberOfUsers = numberOfUsers;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public List<Groups> getGroups() {
    return groups;
  }

  public void setGroups(List<Groups> groups) {
    this.groups = groups;
  }

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public Foundation getFoundation() {
    return foundation;
  }

  public void setFoundation(Foundation foundation) {
    this.foundation = foundation;
  }

  public boolean isMessageEnabled() {
    return messageEnabled;
  }

  public void setMessageEnabled(boolean messageEnabled) {
    this.messageEnabled = messageEnabled;
  }

  public Boolean getMessageEnabled() {
    return messageEnabled;
  }

  public void setMessageEnabled(Boolean messageEnabled) {
    this.messageEnabled = messageEnabled;
  }

  public Boolean getMarketEnabled() {
    return marketEnabled;
  }

  public void setMarketEnabled(Boolean marketEnabled) {
    this.marketEnabled = marketEnabled;
  }

  public Boolean getTimeLockEnabled() {
    return timeLockEnabled;
  }

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

  public Integer getOrganizationTimeZone() {
    return organizationTimeZone;
  }

  public void setOrganizationTimeZone(Integer organizationTimeZone) {
    this.organizationTimeZone = organizationTimeZone;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Organization that = (Organization) o;

    if (!getName().equals(that.getName())) {
      return false;
    }
    return getOrgId().equals(that.getOrgId());
  }

  @Override
  public int hashCode() {
    int result = getName().hashCode();
    result = 31 * result + getOrgId().hashCode();
    return result;
  }
}
