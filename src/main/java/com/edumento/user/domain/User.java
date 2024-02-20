package com.edumento.user.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.assessment.domain.Assessment;
import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.b2b.domain.TimeLock;
import com.edumento.b2c.domain.CloudPackage;
import com.edumento.content.domain.Content;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.space.domain.Space;
import com.edumento.user.constant.UserType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity

@Table(name = "user_table")
@DynamicInsert
@DynamicUpdate
public class User extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false, unique = false)
	private String userName;

	@Column
	private String thumbnail;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String fullName;

	@Column
	private String mobile;
	@Column
	private String color;

	@Column(nullable = false)
	private String email;

	@Column
	private Boolean status = Boolean.FALSE;
	@Column
	private String langKey;
	@Column
	private String activationKey;
	@Column
	private String resetKey;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date resetDate;

	@Column
	private Boolean firstLogin = Boolean.TRUE;
	@Column
	private Boolean forceChangePassword = Boolean.FALSE;
	@Column
	private Boolean gender = null;
	@Column
	private Boolean autoJoin = Boolean.FALSE;

	@Column
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date activationDate;

	@Column
	private String profession;
	@Column
	private String country;

	@Column
	private String userStatus;

	@Column(length = 500)
	private String interests;

	@Column
	@Enumerated
	private UserType type;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@ManyToOne
	@JoinColumn(name = "organization", foreignKey = @ForeignKey(name = "FK_USER_ORGANIZATIO"))
	private Organization organization;

	@ManyToOne
	@JoinColumn(name = "foundation_id", foreignKey = @ForeignKey(name = "FK_USER_FOUNDATION"))
	private Foundation foundation;

	@ManyToOne
	@JoinColumn(name = "time_lock", foreignKey = @ForeignKey(name = "FK_USER_TIME_LOCAK"))
	private TimeLock timeLock;

	@ManyToMany(mappedBy = "users")
	private List<Groups> groups = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private List<Space> spaces = new ArrayList<>();

	@OneToMany(mappedBy = "owner")
	private List<Content> contents = new ArrayList<>();

	@ManyToMany(mappedBy = "users")
	private Set<Role> roles = new HashSet<>();

	/** Created by A.Alsayed on 04/01/19. */
	// new field will be used for the challenge task:
	// ==============================================
	@ManyToMany(mappedBy = "challengees")
	private Set<Assessment> assessments = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "cloud_package_id", foreignKey = @ForeignKey(name = "FK_USER_PACkAGE"))
	private CloudPackage cloudPackage;

	@Column
	private Boolean notification = Boolean.TRUE;

	@Column
	private Boolean mailNotification = true;

	@Column
	private String chatId;

	/*
	 * new column "School" created by Ahmad alsayed
	 */
	@Column
	private String school;

	/**
	 * created by A.Alsayed 23-01-2019 New Column to store / update user scores per
	 * each practice and each challenge.
	 */
	@Column
	private Float totalScore = 0.0f;

	public static User of(Long id) {
		var user = new User();
		user.setId(id);
		return user;
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

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getLangKey() {
		return langKey;
	}

	public void setLangKey(String langKey) {
		this.langKey = langKey;
	}

	public String getActivationKey() {
		return activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	public String getResetKey() {
		return resetKey;
	}

	public void setResetKey(String resetKey) {
		this.resetKey = resetKey;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public Boolean getFirstLogin() {
		return firstLogin;
	}

	public void setFirstLogin(Boolean firstLogin) {
		this.firstLogin = firstLogin;
	}

	public Boolean getForceChangePassword() {
		return forceChangePassword;
	}

	public void setForceChangePassword(Boolean forceChangePassword) {
		this.forceChangePassword = forceChangePassword;
	}

	public Boolean getGender() {
		return gender;
	}

	public void setGender(Boolean gender) {
		this.gender = gender;
	}

	public Boolean getAutoJoin() {
		return autoJoin;
	}

	public void setAutoJoin(Boolean autoJoin) {
		this.autoJoin = autoJoin;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
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

	public UserType getType() {
		return type;
	}

	public void setType(UserType type) {
		this.type = type;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Boolean getNotification() {
		return notification;
	}

	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	public Boolean getMailNotification() {
		return mailNotification;
	}

	public void setMailNotification(Boolean mailNotification) {
		this.mailNotification = mailNotification;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public Float getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(Float totalScore) {
		this.totalScore = totalScore;
	}

	/**
	 * @return the organization
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * @param organization the organization to set
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	/**
	 * @return the foundation
	 */
	public Foundation getFoundation() {
		return foundation;
	}

	/**
	 * @param foundation the foundation to set
	 */
	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	/**
	 * @return the timeLock
	 */
	public TimeLock getTimeLock() {
		return timeLock;
	}

	/**
	 * @param timeLock the timeLock to set
	 */
	public void setTimeLock(TimeLock timeLock) {
		this.timeLock = timeLock;
	}

	/**
	 * @return the groups
	 */
	public List<Groups> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(List<Groups> groups) {
		this.groups = groups;
	}

	/**
	 * @return the spaces
	 */
	public List<Space> getSpaces() {
		return spaces;
	}

	/**
	 * @param spaces the spaces to set
	 */
	public void setSpaces(List<Space> spaces) {
		this.spaces = spaces;
	}

	/**
	 * @return the contents
	 */
	public List<Content> getContents() {
		return contents;
	}

	/**
	 * @param contents the contents to set
	 */
	public void setContents(List<Content> contents) {
		this.contents = contents;
	}

	/**
	 * @return the assessments
	 */
	public Set<Assessment> getAssessments() {
		return assessments;
	}

	/**
	 * @param assessments the assessments to set
	 */
	public void setAssessments(Set<Assessment> assessments) {
		this.assessments = assessments;
	}

	/**
	 * @return the cloudPackage
	 */
	public CloudPackage getCloudPackage() {
		return cloudPackage;
	}

	/**
	 * @param cloudPackage the cloudPackage to set
	 */
	public void setCloudPackage(CloudPackage cloudPackage) {
		this.cloudPackage = cloudPackage;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		var other = (User) obj;
		if (id == null) {
			return other.id == null;
		} else {
			return id.equals(other.id);
		}
	}

}
