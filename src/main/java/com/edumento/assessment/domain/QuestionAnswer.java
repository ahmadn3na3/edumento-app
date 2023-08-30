package com.edumento.assessment.domain;

import com.edumento.core.domain.AbstractEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/** Created by ayman on 03/07/16. */
@Document
public class QuestionAnswer extends AbstractEntity {
  @Id private String id;
  @Indexed private Long userId;
  @Indexed private Long questionId;

  private Boolean skipped = Boolean.FALSE;

  private String userAnswer;

  private Float grade = 0f;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserAnswer() {
    return userAnswer;
  }

  public void setUserAnswer(String userAnswer) {
    this.userAnswer = userAnswer;
  }

  public Long getQuestionId() {
    return questionId;
  }

  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
  }

  public Float getGrade() {
    return grade;
  }

  public void setGrade(Float grade) {
    this.grade = grade;
  }

  public Boolean getSkipped() {
    return skipped;
  }

  public void setSkipped(Boolean skipped) {
    this.skipped = skipped;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    QuestionAnswer that = (QuestionAnswer) o;

    if (!userId.equals(that.userId)) return false;
    return questionId.equals(that.questionId);
  }

  @Override
  public int hashCode() {
    int result = userId.hashCode();
    result = 31 * result + questionId.hashCode();
    return result;
  }
}
