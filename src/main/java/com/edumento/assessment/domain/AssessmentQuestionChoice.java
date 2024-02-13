package com.edumento.assessment.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.constants.PairColumn;
import com.edumento.core.constants.ResourceType;
import com.edumento.core.domain.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Created by ayman on 29/06/16. */
@Entity
@Table(name = "assessment_question_choice")
@DynamicInsert
@DynamicUpdate
public class AssessmentQuestionChoice extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(
      name = "assessment_question",
      nullable = false,
      foreignKey = @ForeignKey(name = "FK_ASSESSMENT_QUESTION"))
  private AssessmentQuestion assessmentQuestion;

  @Column private String label;

  @Column private Boolean correctAnswer;

  @Column private Integer correctOrder;

  @Column private String correctAnswerResourceUrl;

  @Enumerated(EnumType.STRING)
  private ResourceType resourceType;

  @Column private String correctAnswerDescription;

  @Enumerated private PairColumn pairCol;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AssessmentQuestion getAssessmentQuestion() {
    return assessmentQuestion;
  }

  public void setAssessmentQuestion(AssessmentQuestion assessmentQuestion) {
    this.assessmentQuestion = assessmentQuestion;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Boolean getCorrectAnswer() {
    return correctAnswer;
  }

  public void setCorrectAnswer(Boolean correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public Integer getCorrectOrder() {
    return correctOrder;
  }

  public void setCorrectOrder(Integer correctOrder) {
    this.correctOrder = correctOrder;
  }

  public String getCorrectAnswerResourceUrl() {
    return correctAnswerResourceUrl;
  }

  public void setCorrectAnswerResourceUrl(String correctAnswerResourceUrl) {
    this.correctAnswerResourceUrl = correctAnswerResourceUrl;
  }

  public PairColumn getPairCol() {
    return pairCol;
  }

  public void setPairCol(PairColumn pairCol) {
    this.pairCol = pairCol;
  }

  public String getCorrectAnswerDescription() {
    return correctAnswerDescription;
  }

  public void setCorrectAnswerDescription(String correctAnswerDescription) {
    this.correctAnswerDescription = correctAnswerDescription;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  @Override
  public String toString() {
    return "AssessmentQuestionChoice{"
        + "id="
        + id
        + ", assessmentQuestion="
        + assessmentQuestion
        + ", label='"
        + label
        + '\''
        + ", correctAnswer="
        + correctAnswer
        + ", correctOrder="
        + correctOrder
        + ", correctAnswerResourceUrl='"
        + correctAnswerResourceUrl
        + '\''
        + ", pairCol="
        + pairCol
        + "} "
        + super.toString();
  }
}
