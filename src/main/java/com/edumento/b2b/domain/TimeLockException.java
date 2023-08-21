package com.edumento.b2b.domain;

import java.util.Date;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.edumento.core.constants.LockStatus;
import com.edumento.core.domain.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@DynamicInsert
@DynamicUpdate
public class TimeLockException extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column
  private String name;

  @ManyToOne
  @JoinColumn(name = "time_lock", foreignKey = @ForeignKey(name = "FK_TIME_LOCK_EXCEPTION"))
  private TimeLock timeLock;

  @Column
  @Enumerated(EnumType.ORDINAL)
  private LockStatus lockStatus;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date fromDate;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date toDate;

  @Column
  private String fromTime;
  @Column
  private String toTime;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TimeLock getTimeLock() {
    return timeLock;
  }

  public void setTimeLock(TimeLock timeLock) {
    this.timeLock = timeLock;
  }

  public LockStatus getLockStatus() {
    return lockStatus;
  }

  public void setLockStatus(LockStatus lockStatus) {
    this.lockStatus = lockStatus;
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

  public String getFromTime() {
    return fromTime;
  }

  public void setFromTime(String fromTime) {
    this.fromTime = fromTime;
  }

  public String getToTime() {
    return toTime;
  }

  public void setToTime(String toTime) {
    this.toTime = toTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    TimeLockException that = (TimeLockException) o;

    if (getTimeLock() != null ? !getTimeLock().equals(that.getTimeLock())
        : that.getTimeLock() != null)
      return false;
    if (getFromDate() != null ? !getFromDate().equals(that.getFromDate())
        : that.getFromDate() != null)
      return false;
    if (getToDate() != null ? !getToDate().equals(that.getToDate()) : that.getToDate() != null)
      return false;
    if (!getFromTime().equals(that.getFromTime()))
      return false;
    return getToTime().equals(that.getToTime());
  }

  @Override
  public int hashCode() {
    int result = getTimeLock() != null ? getTimeLock().hashCode() : 0;
    result = 31 * result + (getFromDate() != null ? getFromDate().hashCode() : 0);
    result = 31 * result + (getToDate() != null ? getToDate().hashCode() : 0);
    result = 31 * result + getFromTime().hashCode();
    result = 31 * result + getToTime().hashCode();
    return result;
  }
}
