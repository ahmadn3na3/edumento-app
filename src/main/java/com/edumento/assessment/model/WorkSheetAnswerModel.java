package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.List;

/** Created by ahmad on 11/27/16. */
public class WorkSheetAnswerModel {
//  private List<InkModel> inks = new ArrayList<>();
	private List<TextAnswerModel> textAnswers = new ArrayList<>();

//  public List<InkModel> getInks() {
//    return inks;
//  }
//
//  public void setInks(List<InkModel> inks) {
//    this.inks = inks;
//  }

	public List<TextAnswerModel> getTextAnswers() {
		return textAnswers;
	}

	public void setTextAnswers(List<TextAnswerModel> textAnswers) {
		this.textAnswers = textAnswers;
	}

	@Override
	public String toString() {
		return "WorkSheetAnswerModel{" + ", textAnswers=" + textAnswers + '}';
	}
}
