package com.edumento.assessment.model;


import com.edumento.core.constants.PairColumn;
import com.edumento.core.constants.ResourceType;

/** Created by ayman on 14/06/16. */
public class ChoicesModel {
  private Long id;
  private String label;
  private Boolean correctAnswer;
  private String correctAnswerResourceUrl;
  private PairColumn pairColumn;
  private String correctAnswerDescription;
  private Integer correctOrder;
  private ResourceType resourceType;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getCorrectAnswerDescription() {
    return correctAnswerDescription;
  }

  public void setCorrectAnswerDescription(String correctAnswerDescription) {
    this.correctAnswerDescription = correctAnswerDescription;
  }

  public PairColumn getPairColumn() {
    return pairColumn;
  }

  public void setPairColumn(PairColumn pairColumn) {
    this.pairColumn = pairColumn;
  }

  public ResourceType getResourceType() {
    return resourceType;
  }

  public void setResourceType(ResourceType resourceType) {
    this.resourceType = resourceType;
  }

  @Override
  public String toString() {
    return "ChoicesModel{"
        + "id="
        + id
        + ", label='"
        + label
        + '\''
        + ", correctAnswer="
        + correctAnswer
        + ", correctAnswerResourceUrl='"
        + correctAnswerResourceUrl
        + '\''
        + ", pairColumn="
        + pairColumn
        + ", correctOrder="
        + correctOrder
        + '}';
  }
}
