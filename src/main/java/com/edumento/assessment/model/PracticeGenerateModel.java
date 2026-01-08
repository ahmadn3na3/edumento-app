package com.edumento.assessment.model;

/** Created by ayman on 16/08/17. */
public class PracticeGenerateModel {
  private String practiceName;
  private Long spaceId;
  private Integer minimum = 5;

  private QuestionSearchModel questionSearchModel;

  public PracticeGenerateModel() {}

  public String getPracticeName() {
    return practiceName;
  }

  public void setPracticeName(String practiceName) {
    this.practiceName = practiceName;
  }

  public QuestionSearchModel getQuestionSearchModel() {
    return questionSearchModel;
  }

  public void setQuestionSearchModel(QuestionSearchModel questionSearchModel) {
    this.questionSearchModel = questionSearchModel;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public Integer getMinimum() {
    return minimum;
  }

  public void setMinimum(Integer minimum) {
    this.minimum = minimum;
  }
}
