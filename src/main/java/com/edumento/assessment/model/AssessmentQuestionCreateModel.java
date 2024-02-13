package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.List;

import com.edumento.core.constants.QuestionType;
import com.edumento.core.constants.ResourceType;

import jakarta.validation.constraints.NotNull;


/** Created by ayman on 29/06/16. */
public class AssessmentQuestionCreateModel {

  private Long Id;

  @NotNull(message = "error.question.body.null")
  private String body;

  private QuestionType questionType;

  private String correctAnswer;

  private String bodyResourceUrl;

  private ResourceType resourceType;

  private List<ChoicesModel> choicesList = new ArrayList<>();

  private Integer questionWeight = 0;

  public AssessmentQuestionCreateModel() {}

  public AssessmentQuestionCreateModel(MongoQuestionModel questionModel) {
    this.body = questionModel.getBody();
    this.questionType = questionModel.getQuestionType();
    this.correctAnswer = questionModel.getCorrectAnswer();
    this.bodyResourceUrl = questionModel.getBodyResourceUrl();
    this.choicesList = questionModel.getChoicesList();
  }

  public Integer getQuestionWeight() {
    return questionWeight;
  }

  public void setQuestionWeight(Integer questionWeight) {
    this.questionWeight = questionWeight;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public QuestionType getQuestionType() {
    return questionType;
  }

  public void setQuestionType(QuestionType questionType) {
    this.questionType = questionType;
  }

  public String getCorrectAnswer() {
    return correctAnswer;
  }

  public void setCorrectAnswer(String correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public String getBodyResourceUrl() {
    return bodyResourceUrl;
  }

  public void setBodyResourceUrl(String bodyResourceUrl) {
    this.bodyResourceUrl = bodyResourceUrl;
  }

  public List<ChoicesModel> getChoicesList() {
    return choicesList;
  }

  public void setChoicesList(List<ChoicesModel> choicesList) {
    this.choicesList = choicesList;
  }

  public Long getId() {
    return Id;
  }

  public void setId(Long id) {
    Id = id;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  @Override
  public String toString() {
    return "AssessmentQuestionCreateModel{"
        + "questionWeight="
        + questionWeight
        + "} "
        + super.toString();
  }
}
