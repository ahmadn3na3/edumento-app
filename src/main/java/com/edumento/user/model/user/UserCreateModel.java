package com.edumento.user.model.user;

import java.time.ZonedDateTime;

import com.edumento.core.constants.Gender;
import com.edumento.user.constant.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Created by ahmad on 2/17/16. */
public class UserCreateModel {
	public static final int PASSWORD_MIN_LENGTH = 8;
	public static final int PASSWORD_MAX_LENGTH = 24;

	@Pattern(regexp = "^[a-zA-Z0-9]*(@[A-Za-z]*)?$", message = "error.login.pattern")
	@NotNull(message = "error.login.null")
	@Size(max = 50, message = "error.login.length")
	@NotEmpty()
	private String username;

	@NotNull(message = "error.fname.null")
	@Size(max = 50, message = "error.fname.length")
	@NotEmpty
	private String fullName;

	@NotNull(message = "error.email.null")
	@Email(message = "error.email.invalid")
	@NotEmpty
	private String email;

	private String mobile;

	private Gender gender = Gender.MALE;

	private UserType type = UserType.USER;

	private ZonedDateTime birthDate;
	private String profession;
	private String country;
	private String userStatus;
	private String interests;
	private String image;
	private String lang = "en";
	private Boolean notification = Boolean.TRUE;
	private Boolean emailNotification = Boolean.TRUE;
	/*
	 * new attribute "School" created by Ahmad Alsayed
	 */
	private String school;

	public String getUsername() {
		return username;
	}

	public void setUsername(String login) {
		username = login.trim().toLowerCase();
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName.trim();
	}

	public String getEmail() {
		return email.trim().toLowerCase();
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public ZonedDateTime getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(ZonedDateTime birthDate) {
		this.birthDate = birthDate;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
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

	public void setEmailNotification(Boolean emailNotification) {
		this.emailNotification = emailNotification;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	@Override
	public String toString() {
		return String.format(
				"UserCreateModel [username=%s, fullName=%s, email=%s, mobile=%s, gender=%s, type=%s, birthDate=%s, profession=%s, country=%s, userStatus=%s, interests=%s, image=%s, lang=%s]",
				username, fullName, email, mobile, gender, type, birthDate, profession, country, userStatus, interests,
				image, lang);
	}
}
