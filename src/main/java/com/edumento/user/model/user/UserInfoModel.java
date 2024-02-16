package com.edumento.user.model.user;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import com.edumento.core.constants.Gender;
import com.edumento.core.constants.SpaceRole;
import com.edumento.user.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

/** Created by ahmad on 2/17/16. */
public class UserInfoModel {

	private Long id;

	private String login;

	private String fullName;
	private String userType;
	private String email;
	private Gender gender;

	private boolean activated;

	private String mobile;
	private String lang;
	@JsonIgnore
	private String resetKey;
	@JsonIgnore
	private String activationKey;
	private String country;
	private String profession;
	private ZonedDateTime birthDate;
	private String image;
	private String userStatus;
	private String interests;

	private Map<String, Object> permissions = new HashMap<>();
	private Map<SpaceRole, Map<String, Object>> spaceRolePermission = new HashMap<>();

	private Boolean notification;
	private Boolean emailNotification;
	private Boolean autoJoin;

	public UserInfoModel() {
	}

	public UserInfoModel(User user) {
		if (user != null) {
			this.id = user.getId();
			this.login = user.getUserName();
			this.fullName = user.getFullName();
			this.userType = user.getType().name();
			this.email = user.getEmail();
			this.activated = user.getStatus() != null && user.getStatus();
			this.lang = user.getLangKey();
			this.resetKey = user.getResetKey();
			this.activationKey = user.getActivationKey();
			this.gender = user.getGender() == null ? Gender.MALE : user.getGender() ? Gender.MALE : Gender.FEMALE;
			this.mobile = user.getMobile();
			this.profession = user.getProfession();
			this.country = user.getCountry();
			this.image = user.getThumbnail();
			this.userStatus = user.getUserStatus();
			this.interests = user.getInterests();
			this.birthDate = ZonedDateTime.ofInstant(user.getBirthDate().toInstant(), ZoneOffset.UTC);
			this.notification = user.getNotification();
			this.emailNotification = user.getMailNotification();
			this.setAutoJoin(user.getAutoJoin());
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Map<String, Object> getPermissions() {

		return permissions;
	}

	public void setPermissions(Map<String, Object> permissions) {
		this.permissions = permissions;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public ZonedDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(ZonedDateTime birthDate) {
		this.birthDate = birthDate;
	}

	/** @return the image */
	public String getImage() {
		return image;
	}

	/** @param image the image to set */
	public void setImage(String image) {
		this.image = image;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public Map<SpaceRole, Map<String, Object>> getSpaceRolePermission() {
		return spaceRolePermission;
	}

	public void setSpaceRolePermission(Map<SpaceRole, Map<String, Object>> spaceRolePermission) {
		this.spaceRolePermission = spaceRolePermission;
	}

	public Boolean getNotification() {
		return notification;
	}

	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	public Boolean getEmailNotification() {
		return emailNotification;
	}

	public void setEmailNotification(Boolean mailNotification) {
		this.emailNotification = mailNotification;
	}

	public Boolean getAutoJoin() {
		return autoJoin;
	}

	public void setAutoJoin(Boolean autoJoin) {
		this.autoJoin = autoJoin;
	}

	@Override
	public String toString() {
		return "UserInfoModel{" + "id=" + id + ", login='" + login + '\'' + ", fullName='" + fullName + '\''
				+ ", userType='" + userType + '\'' + ", email='" + email + '\'' + ", gender=" + gender + ", activated="
				+ activated + ", mobile='" + mobile + '\'' + ", lang='" + lang + '\'' + ", resetKey='" + resetKey + '\''
				+ ", activationKey='" + activationKey + '\'' + ", permissions=" + permissions + ",notifications="
				+ notification + ",mailNotification=" + emailNotification + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		UserInfoModel that = (UserInfoModel) o;

		if (!getId().equals(that.getId()))
			return false;
		return getLogin().equals(that.getLogin());
	}

	@Override
	public int hashCode() {
		int result = getId().hashCode();
		result = 31 * result + getLogin().hashCode();
		return result;
	}
}
