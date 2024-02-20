package com.edumento.b2b.model.foundation;

import java.time.ZonedDateTime;
import java.util.Date;

import com.edumento.core.util.DateConverter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Created by ayman on 02/06/16. */
public class FoundationCreateModel {

	@NotNull(message = "error.foundation.name.null")
	private String name;

	@NotNull(message = "error.foundation.code.null")
	@Size(max = 6, message = "error.foundation.code.length")
	@Pattern(regexp = "[A-Za-z]*", message = "error.foundation.code.type")
	private String code;

	private ZonedDateTime startDate = DateConverter.convertDateToZonedDateTime(new Date());

	@NotNull(message = "error.foundation.endate.null")
	private ZonedDateTime endDate;

	@NotNull(message = "error.foundation.foundationpackage.null")
	private Long foundationPackageId;

	private Boolean messageEnabled = Boolean.FALSE;

	private Boolean marketEnabled = Boolean.FALSE;

	private Boolean timeLockEnabled = Boolean.FALSE;

	private Boolean genderSensitivity = Boolean.FALSE;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
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

	public Long getFoundationPackageId() {
		return foundationPackageId;
	}

	public void setFoundationPackageId(Long foundationPackageId) {
		this.foundationPackageId = foundationPackageId;
	}

	@Override
	public String toString() {
		return "FoundationCreateModel{" + "name='" + name + '\'' + ", code='" + code + '\'' + ", startDate=" + startDate
				+ ", endDate=" + endDate + ", messageEnabled=" + messageEnabled + ", marketEnabled=" + marketEnabled
				+ ", timeLockEnabled=" + timeLockEnabled + ", genderSensitivity=" + genderSensitivity + '}';
	}
}
