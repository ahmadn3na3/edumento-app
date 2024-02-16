package com.edumento.assessment.model.challenge;


import com.edumento.assessment.model.QuestionSearchModel;

public class ChallengeCreateModel {
  private String title;
  private Long limitDuration;
  private Long spaceId;
  private QuestionSearchModel questionSearchModel;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getLimitDuration() {
    if (this.limitDuration == null || this.limitDuration == 0L) {
      return 600000L;
    }
    return limitDuration;
  }

  public void setLimitDuration(Long limitDuration) {
    this.limitDuration = limitDuration;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public QuestionSearchModel getQuestionSearchModel() {
    return questionSearchModel;
  }

  public void setQuestionSearchModel(QuestionSearchModel questionSearchModel) {
    this.questionSearchModel = questionSearchModel;
  }
}
