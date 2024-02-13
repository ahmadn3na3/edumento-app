package com.edumento.assessment.model;

import java.time.ZonedDateTime;

import com.edumento.assessment.domain.AssessmentQuestion;
import com.edumento.assessment.domain.AssessmentQuestionChoice;
import com.edumento.core.model.SimpleModel;
import com.edumento.core.util.DateConverter;


/** Created by ayman on 13/06/16. */
public class QuestionModel extends QuestionCreateModel {
  private Long id;

  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;
  private String lastModifiedBy;
  private SimpleModel category;
  private SimpleModel owner;

  public QuestionModel() {}

  public QuestionModel(AssessmentQuestion q) {
    this.setId(q.getId());
    this.setBody(q.getBody());
    this.setPublic(q.getPublic());
    this.setQuestionType(q.getQuestionType());
    this.setCorrectAnswer(q.getCorrectAnswer());
    this.setBodyResourceUrl(q.getBodyResourceUrl());
    this.setCreationDate(DateConverter.convertDateToZonedDateTime(q.getCreationDate()));
    this.setLastModifiedDate(DateConverter.convertDateToZonedDateTime(q.getLastModifiedDate()));
    this.setLastModifiedBy(q.getLastModifiedBy());
    this.setResourceType(q.getResourceType());

    if (q.getAssessmentQuestionChoices() != null) {

      for (AssessmentQuestionChoice choice : q.getAssessmentQuestionChoices()) {
        if (choice == null) {
          continue;
        }
        ChoicesModel choicesModel = new ChoicesModel();
        choicesModel.setId(choice.getId());
        choicesModel.setCorrectAnswer(choice.getCorrectAnswer());
        choicesModel.setCorrectAnswerResourceUrl(choice.getCorrectAnswerResourceUrl());
        choicesModel.setCorrectOrder(choice.getCorrectOrder());
        choicesModel.setLabel(choice.getLabel());
        choicesModel.setCorrectAnswerDescription(choice.getCorrectAnswerDescription());
        choicesModel.setPairColumn(choice.getPairCol());
        choicesModel.setResourceType(choice.getResourceType());
        this.getChoicesList().add(choicesModel);
      }
    }
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public SimpleModel getCategory() {
    return category;
  }

  public void setCategory(SimpleModel category) {
    this.category = category;
  }

  public SimpleModel getOwner() {
    return owner;
  }

  public void setOwner(SimpleModel owner) {
    this.owner = owner;
  }

  @Override
  public String toString() {
    return "QuestionModel{"
        + "id="
        + id
        + ", creationDate="
        + creationDate
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", lastModifiedBy='"
        + lastModifiedBy
        + '\''
        + "} "
        + super.toString();
  }
}
