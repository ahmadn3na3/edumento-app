package com.edumento.core.model;

/** Created by ahmad on 6/13/16. */
public class ToggleStatusModel extends IdModel {
	private Boolean status = Boolean.FALSE;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ToggleStatusModel{" + "status=" + status + "} " + super.toString();
	}
}
