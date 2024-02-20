package com.edumento.core.domain;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** Created by ahmad on 2/24/16. */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate = new Date();

	@Column
	private String createBy = "System";

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastModifiedDate = null;

	@Column
	private String lastModifiedBy;
	@Column
	private boolean deleted;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date deletedDate;

	@Column
	private String deletedBy;

	protected AbstractEntity() {
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Date getDeletedDate() {
		return deletedDate;
	}

	public void setDeletedDate(Date deletedDate) {
		this.deletedDate = deletedDate;
	}

	public String getDeletedBy() {
		return deletedBy;
	}

	public void setDeletedBy(String deletedBy) {
		this.deletedBy = deletedBy;
	}

	@PrePersist
	public void prePersist() {
//    setCreateBy(
//        SecurityUtils.getCurrentUserLogin() == null
//            ? "System"
//            : SecurityUtils.getCurrentUserLogin());
//    setCreationDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now()));
	}

	@PreUpdate
	public void preUpdate() {
//    setLastModifiedBy(
//        SecurityUtils.getCurrentUserLogin() == null
//            ? "System"
//            : SecurityUtils.getCurrentUserLogin());
//    setLastModifiedDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now()));
	}

	@PreRemove
	public void preRemove() {
//    setDeletedBy(
//        SecurityUtils.getCurrentUserLogin() == null
//            ? "System"
//            : SecurityUtils.getCurrentUserLogin());
//    setDeletedDate(DateConverter.convertZonedDateTimeToDate(ZonedDateTime.now()));
	}
}
