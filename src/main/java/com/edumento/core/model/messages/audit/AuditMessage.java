package com.edumento.core.model.messages.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.edumento.core.constants.notification.EntityAction;

/** Created by ahmadsalah on 7/3/17. */
public class AuditMessage implements Serializable {
	private Long id;
	private String userName;
	private EntityAction auditValue;
	private Date auditDate;
	private Long orgId;
	private Long foundationId;
	private String clientId;
	private Map<String, String> data;

	public AuditMessage() {
		auditDate = new Date();
	}

	public AuditMessage(Long id, String userName, Long orgId, Long foundationId, EntityAction auditValue,
			Map<String, String> data) {
		this();
		this.id = id;
		this.userName = userName;
		this.auditValue = auditValue;
		this.orgId = orgId;
		this.foundationId = foundationId;
		this.data = data;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public EntityAction getAuditValue() {
		return auditValue;
	}

	public void setAuditValue(EntityAction auditValue) {
		this.auditValue = auditValue;
	}

	public Map<String, String> getData() {
		return data;
	}

	public void setData(Map<String, String> data) {
		this.data = data;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public Long getFoundationId() {
		return foundationId;
	}

	public void setFoundationId(Long foundationId) {
		this.foundationId = foundationId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
}
