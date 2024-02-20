package com.edumento.b2b.model.organization;

/** Created by ayman on 20/06/16. */
public class OrganizationSettingModel {
	private Long id;

	private String name;

	private boolean messageEnabled;

	private boolean marketEnabled;

	private boolean timeLockEnabled;

	private boolean genderSensitivity;

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

	public boolean isMessageEnabled() {
		return messageEnabled;
	}

	public void setMessageEnabled(boolean messageEnabled) {
		this.messageEnabled = messageEnabled;
	}

	public boolean isMarketEnabled() {
		return marketEnabled;
	}

	public void setMarketEnabled(boolean marketEnabled) {
		this.marketEnabled = marketEnabled;
	}

	public boolean isTimeLockEnabled() {
		return timeLockEnabled;
	}

	public void setTimeLockEnabled(boolean timeLockEnabled) {
		this.timeLockEnabled = timeLockEnabled;
	}

	public boolean isGenderSensitivity() {
		return genderSensitivity;
	}

	public void setGenderSensitivity(boolean genderSensitivity) {
		this.genderSensitivity = genderSensitivity;
	}

	@Override
	public String toString() {
		return "OrganizationSettingModel{" + "id=" + id + ", name='" + name + '\'' + ", messageEnabled="
				+ messageEnabled + ", marketEnabled=" + marketEnabled + ", timeLockEnabled=" + timeLockEnabled
				+ ", genderSensitivity=" + genderSensitivity + '}';
	}
}
