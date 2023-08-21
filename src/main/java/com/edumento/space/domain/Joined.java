package com.edumento.space.domain;

import java.util.Date;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.edumento.core.constants.JoinedStatus;
import com.edumento.core.constants.SpaceRole;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/** Created by ahmad on 3/21/16. */
@Entity
@DynamicInsert
@DynamicUpdate
public class Joined extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private Boolean favorite = Boolean.FALSE;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessed;

	@Column
	private Integer rating;

	@ManyToOne
	@JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_JOIN_USER"))
	private User user;

	@ManyToOne
	@JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "FK_JOIN_SPACE"))
	private Space space;

	private String groupName;

	@Enumerated
	private SpaceRole spaceRole = SpaceRole.COLLABORATOR;
	@Enumerated
	private JoinedStatus joinedStatus = JoinedStatus.JOINED;

	@Column
	private Boolean notification = true;
	@Column
	private Integer discussionsCount = 0;
	@Column
	private Integer annotationsCount = 0;
	@Column
	private Integer assessmentCount = 0;
	@Column
	private Integer addContentCount = 0;
	@Column
	private Integer discussionCommentsCount = 0;
	@Column
	private Integer spaceViewsCount = 0;

	/* created by A.Alsayed 15-01-2019 */
	// new attribute total space score for recording all practices / challenges
	// scores for user per space.
	@Column
	private Float spaceScorePoints = 0.0f;

	public Joined() {
	}

	public Joined(User user, Space space) {
		this.user = user;
		this.space = space;
	}

	public Boolean getFavorite() {
		return favorite;
	}

	public void setFavorite(Boolean favorite) {
		this.favorite = favorite;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SpaceRole getSpaceRole() {
		return spaceRole;
	}

	public void setSpaceRole(SpaceRole spaceRole) {
		this.spaceRole = spaceRole;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public JoinedStatus getJoinedStatus() {
		return joinedStatus;
	}

	public void setJoinedStatus(JoinedStatus joinedStatus) {
		this.joinedStatus = joinedStatus;
	}

	public Boolean getNotification() {
		return notification;
	}

	public void setNotification(Boolean notification) {
		this.notification = notification;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Joined joined = (Joined) o;

		if (!getUser().equals(joined.getUser())) {
			return false;
		}
		return getSpace().equals(joined.getSpace());
	}

	public Integer getDiscussionsCount() {
		return discussionsCount;
	}

	public void setDiscussionsCount(Integer discussionsCount) {
		if (discussionsCount != null)
			this.discussionsCount = discussionsCount;
	}

	public Integer getAnnotationsCount() {
		return annotationsCount;
	}

	public void setAnnotationsCount(Integer annotationsCount) {
		if (annotationsCount != null)
			this.annotationsCount = annotationsCount;
	}

	public Integer getAddContentCount() {
		return addContentCount;
	}

	public void setAddContentCount(Integer addContentCount) {
		if (addContentCount != null)
			this.addContentCount = addContentCount;
	}

	public Integer getSpaceViewsCount() {
		return spaceViewsCount;
	}

	public void setSpaceViewsCount(Integer spaceViewsCount) {
		if (spaceViewsCount != null)
			this.spaceViewsCount = spaceViewsCount;
	}

	public Integer getAssessmentCount() {
		if (assessmentCount == null)
			return 0;
		return assessmentCount;
	}

	public void setAssessmentCount(Integer assessmentCount) {
		if (assessmentCount == null)
			this.assessmentCount = 0;
		else
			this.assessmentCount = assessmentCount;
	}

	public Integer getDiscussionCommentsCount() {
		if (discussionCommentsCount == null)
			return 0;
		return discussionCommentsCount;
	}

	public void setDiscussionCommentsCount(Integer discussionCommentsCount) {
		if (discussionCommentsCount == null)
			this.discussionCommentsCount = 0;
		else
			this.discussionCommentsCount = discussionCommentsCount;
	}

	public Float getSpaceScorePoints() {
		if (spaceScorePoints == null)
			return 0.0f;
		return spaceScorePoints;
	}

	public void setSpaceScorePoints(Float spaceScorePoints) {
		if (spaceScorePoints == null)
			this.spaceScorePoints = 0.0f;
		else
			this.spaceScorePoints = spaceScorePoints;
	}

	@Override
	public int hashCode() {
		int result = getUser().hashCode();
		result = 31 * result + getSpace().hashCode();
		return result;
	}
}
