package com.edumento.assessment.domain;


import com.edumento.content.domain.Content;
import com.edumento.core.constants.AssessmentStatus;
import com.edumento.core.constants.AssessmentType;
import com.edumento.core.domain.AbstractEntity;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/** Created by ayman on 13/06/16. */
@Entity
@Table(name = "assessment")
@DynamicInsert
@DynamicUpdate
public class Assessment extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column private String title;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date dueDate;

  @Enumerated private AssessmentType assessmentType;
  @Enumerated private AssessmentStatus assessmentStatus = AssessmentStatus.NEW;
  @Column private Boolean lockMint;
  @Column private Long limitDuration;
  @Column private Float passingGrade;
  @Column private Date startDateTime;
  @Column private Boolean dateOnly;
  @Column private Boolean randomizingQuestion = Boolean.FALSE;
  @Column private Boolean viewAnswersAfterSubmit = Boolean.TRUE;

  @ManyToOne
  @JoinColumn(name = "space", foreignKey = @ForeignKey(name = "FK_SPACE"))
  private Space space;

  @ManyToOne
  @JoinColumn(name = "owner", foreignKey = @ForeignKey(name = "FK_OWNER_USER"))
  private User owner;
  
  /** Created by A.Alsayed on 04/01/19. */
  // new field will be used for the challenge task:
  // ==============================================
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(
    name = "challengees",
    joinColumns = {
      @JoinColumn(
        name = "assessment_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "FK_ASSESSMENT_USER")
      )
    },
    inverseJoinColumns = {
      @JoinColumn(
        name = "challengee_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "FK_USER_ASSESSMENT")
      )
    }
  )
  private Set<User> challengees = new HashSet<>();
  
  private Integer totalPoints = 0;

  @Column private Boolean publish = Boolean.FALSE;

  @OneToOne
  @JoinColumn(name = "content_id", foreignKey = @ForeignKey(name = "FK_WORKSHEET_CONTENT"))
  private Content content;

  @OneToMany(mappedBy = "assessment")
  private List<AssessmentQuestion> assessmentQuestions;

  @Temporal(TemporalType.TIMESTAMP)
  private Date publishDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public AssessmentType getAssessmentType() {
    return assessmentType;
  }

  public void setAssessmentType(AssessmentType assessmentType) {
    this.assessmentType = assessmentType;
  }

  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public List<AssessmentQuestion> getAssessmentQuestions() {
    return assessmentQuestions;
  }

  public void setAssessmentQuestions(List<AssessmentQuestion> assessmentQuestions) {
    this.assessmentQuestions = assessmentQuestions;
  }

  public Boolean getLockMint() {
    return lockMint;
  }

  public void setLockMint(Boolean lockMint) {
    this.lockMint = lockMint;
  }

  public Long getLimitDuration() {
    return limitDuration;
  }

  public void setLimitDuration(Long limitDuration) {
    this.limitDuration = limitDuration;
  }

  public Date getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Date startDateTime) {
    this.startDateTime = startDateTime;
  }

  public Boolean getDateOnly() {
    return dateOnly;
  }

  public void setDateOnly(Boolean dateOnly) {
    this.dateOnly = dateOnly;
  }

  public AssessmentStatus getAssessmentStatus() {
    return assessmentStatus;
  }

  public void setAssessmentStatus(AssessmentStatus assessmentStatus) {
    this.assessmentStatus = assessmentStatus;
  }

  public Boolean getRandomizingQuestion() {
    return randomizingQuestion;
  }

  public void setRandomizingQuestion(Boolean randomizingQuestion) {
    this.randomizingQuestion = randomizingQuestion;
  }

  public Boolean getViewAnswersAfterSubmit() {
    return viewAnswersAfterSubmit;
  }

  public void setViewAnswersAfterSubmit(Boolean viewAnswersAfterSubmit) {
    this.viewAnswersAfterSubmit = viewAnswersAfterSubmit;
  }

  public Boolean getPublish() {
    return publish;
  }

  public void setPublish(Boolean publish) {
    this.publish = publish;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Integer getTotalPoints() {
    return totalPoints;
  }

  public void setTotalPoints(Integer totalPoints) {
    this.totalPoints = totalPoints;
  }

  public Content getContent() {
    return content;
  }

  public void setContent(Content content) {
    this.content = content;
  }

  public Date getPublishDate() {
    return publishDate;
  }

  public void setPublishDate(Date publishDate) {
    this.publishDate = publishDate;
  }

  public Float getPassingGrade() {
    return passingGrade;
  }

  public void setPassingGrade(Float passingGrade) {
    this.passingGrade = passingGrade;
  }  
  
  public Set<User> getChallengees() {
	return challengees;
  }

  public void setChallengees(Set<User> challengees) {
	this.challengees = challengees;
  }

@Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Assessment that = (Assessment) o;

    if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
      return false;
    }
    return getTitle().equals(that.getTitle());
  }

  @Override
  public int hashCode() {
    int result = getId() != null ? getId().hashCode() : 0;
    result = 31 * result + getTitle().hashCode();
    return result;
  }
}
