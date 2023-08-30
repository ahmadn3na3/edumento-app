package com.edumento.assessment.model;

import com.edumento.core.constants.QuestionType;
import java.util.Arrays;

/** Created by ayman on 14/07/16. */
public class QuestionSearchModel {

  private QuestionType[] questionType = QuestionType.values();
  private String body;
  private Long categoryId;
  private Long spaceId;
  private String[] exclude;
  private Integer limit = 0;
  private String[] tags;
  private String[] grades;

  public QuestionType[] getQuestionType() {
    return questionType;
  }

  public void setQuestionType(QuestionType[] questionType) {
    this.questionType = questionType;
  }

  public Long getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(Long categoryId) {
    this.categoryId = categoryId;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }

  public String[] getExclude() {
    return exclude;
  }

  public void setExclude(String[] exclude) {
    this.exclude = exclude;
  }

  public String[] getTags() {
    return tags;
  }

  public void setTags(String[] tags) {
    this.tags = tags;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    if (body == null) {
      this.body = "";
      return;
    }
    this.body = body;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }
  
  

  public String[] getGrades() {
	return grades;
}

public void setGrades(String[] grade) {
	this.grades = grade;
}

@Override
  public String toString() {
    return "QuestionSearchModel{"
        + "questionType="
        + Arrays.toString(questionType)
        + ", categoryId="
        + categoryId
        + ", exclude="
        + Arrays.toString(exclude)
        + ", limit="
        + limit
        + ", tags="
        + Arrays.toString(tags)
        + '}';
  }
}
