package com.edumento.assessment.model;

import com.edumento.core.model.ResponseModel;

import java.util.HashSet;
import java.util.Set;

/** Created by ayman on 16/08/17. */
public class QuestionBankResponseModel extends ResponseModel {
  Set<MongoQuestionModel> data = new HashSet<>();

  @Override
  public Set<MongoQuestionModel> getData() {
    return data;
  }

  public void setData(Set<MongoQuestionModel> data) {
    this.data = data;
  }
}
