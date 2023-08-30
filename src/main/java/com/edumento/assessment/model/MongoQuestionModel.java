package com.edumento.assessment.model;

import com.edumento.core.model.SimpleModel;

import java.time.ZonedDateTime;


/** Created by ayman on 13/06/16. */
public class MongoQuestionModel extends QuestionCreateModel {
  private String id;

  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;
  private String lastModifiedBy;
  private SimpleModel category;
  private SimpleModel owner;

  public MongoQuestionModel() {}

  public String getId() {
    return id;
  }

  public void setId(String id) {
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
