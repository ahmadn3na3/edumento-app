package com.edumento.assessment.model;

import com.edumento.core.constants.AssessmentType;

import java.time.ZonedDateTime;


/** Created by ayman on 03/07/16. */
public class AssessmentGetAllModel {
  private Long spaceId;
  private ZonedDateTime date;
  private AssessmentType assessmentType;

  public AssessmentType getAssessmentType() {
    return assessmentType;
  }

  public void setAssessmentType(AssessmentType assessmentType) {
    this.assessmentType = assessmentType;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  @Override
  public String toString() {
    return "AssessmentGetAllModel{"
        + "spaceId="
        + spaceId
        + ", date="
        + date
        + ", assessmentType="
        + assessmentType
        + '}';
  }
}
