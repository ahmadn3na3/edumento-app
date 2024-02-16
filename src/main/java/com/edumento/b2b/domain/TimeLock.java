package com.edumento.b2b.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.edumento.core.constants.WeekDay;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** @author ahmad */
@Entity
@DynamicInsert
@DynamicUpdate
public class TimeLock extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = false)
  private String name;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date fromDate;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date toDate;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "lock_days", joinColumns = @JoinColumn(name = "time_lock"),
      foreignKey = @ForeignKey(name = "FK_TIME_LOCK_WEEK_DAY"))
  @MapKeyColumn(name = "week_day")
  @Column(length = 4000)
  private Map<WeekDay, String> days = new HashMap<>();

  @OneToMany(mappedBy = "timeLock", cascade = CascadeType.REMOVE)
  private List<TimeLockException> timeLockExceptions;

  @OneToMany(mappedBy = "timeLock")
  private Set<User> users = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "organization", foreignKey = @ForeignKey(name = "FK_TIME_LOCK_ORGANIZATION"))
  private Organization organization;

  @ManyToOne
  @JoinColumn(name = "foundation_id", foreignKey = @ForeignKey(name = "FK_TIME_LOCK_FOUNDATION"))
  private Foundation foundation;

  @Column(length = 4000)
  private String groups;

  @Column(length = 4000)
  private String roles;

  @Column(length = 4000)
  private String userIds;

  @Column
  private String unlockPassword;

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

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

  /** @return the days */
  public Map<WeekDay, String> getDays() {
    return days;
  }

  /** @param days the days to set */
  public void setDays(Map<WeekDay, String> days) {
    this.days = days;
  }

  public List<TimeLockException> getTimeLockExceptions() {
    return timeLockExceptions;
  }

  public void setTimeLockExceptions(List<TimeLockException> timeLockExceptions) {
    this.timeLockExceptions = timeLockExceptions;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public Date getFromDate() {
    return fromDate;
  }

  public void setFromDate(Date fromDate) {
    this.fromDate = fromDate;
  }

  public Date getToDate() {
    return toDate;
  }

  public void setToDate(Date toDate) {
    this.toDate = toDate;
  }

  public String getGroups() {
    return groups;
  }

  public void setGroups(String groups) {
    this.groups = groups;
  }

  public String getRoles() {
    return roles;
  }

  public void setRoles(String roles) {
    this.roles = roles;
  }

  public String getUserIds() {
    return userIds;
  }

  public void setUserIds(String userIds) {
    this.userIds = userIds;
  }

  public Foundation getFoundation() {
    return foundation;
  }

  public void setFoundation(Foundation foundation) {
    this.foundation = foundation;
  }

  public String getUnlockPassword() {
    return unlockPassword;
  }

  public void setUnlockPassword(String unlockPassword) {
    this.unlockPassword = unlockPassword;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((organization == null) ? 0 : organization.hashCode());
    result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof TimeLock other)) {
      return false;
    }
      if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (organization == null) {
        return other.organization == null;
    } else return organization.equals(other.organization);
  }
}
