package com.edumento.b2b.model.timelock;

import java.util.ArrayList;
import java.util.List;

import com.edumento.b2b.model.organization.SimpleOrganizationModel;

/** Created by ahmad on 7/27/16. */
public class TimeLockModel extends TimeLockCreateModel {
	private Long id;
	private SimpleOrganizationModel organizationModel;
	private Integer timeLockExceptionCount = 0;
	private List<TimeLockExceptionModel> timeLockExceptionModels = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SimpleOrganizationModel getOrganizationModel() {
		return organizationModel;
	}

	public void setOrganizationModel(SimpleOrganizationModel organizationModel) {
		this.organizationModel = organizationModel;
	}

	public List<TimeLockExceptionModel> getTimeLockExceptionModels() {
		return timeLockExceptionModels;
	}

	public void setTimeLockExceptionModels(List<TimeLockExceptionModel> timeLockExceptionModels) {
		this.timeLockExceptionModels = timeLockExceptionModels;
	}

	public Integer getTimeLockExceptionCount() {
		return timeLockExceptionCount;
	}

	public void setTimeLockExceptionCount(Integer timeLockExceptionCount) {
		this.timeLockExceptionCount = timeLockExceptionCount;
	}

	@Override
	public String toString() {
		return "TimeLockModel{" + "id=" + id + ", organizationModel=" + organizationModel + ", timeLockExceptionCount="
				+ timeLockExceptionCount + ", timeLockExceptionModels=" + timeLockExceptionModels + "} "
				+ super.toString();
	}
}
