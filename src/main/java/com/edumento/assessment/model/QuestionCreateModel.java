package com.edumento.assessment.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.edumento.core.constants.QuestionType;
import com.edumento.core.constants.ResourceType;

import jakarta.validation.constraints.NotNull;

/** Created by ayman on 13/06/16. */
public class QuestionCreateModel {

	@NotNull(message = "error.question.body.null")
	private String body;

	private Boolean isPublic = Boolean.TRUE;

	@NotNull(message = "error.question.questionType.null")
	private QuestionType questionType;

	private String correctAnswer;

	@NotNull(message = "error.question.category.null")
	private Long categoryId;

	@NotNull(message = "error.question.owner.null")
	private Long ownerId;

	private String bodyResourceUrl;

	private ResourceType resourceType;
	private List<ChoicesModel> choicesList = new ArrayList<>();

	private Set<String> tags = new HashSet<>();

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Boolean getPublic() {
		return isPublic;
	}

	public void setPublic(Boolean aPublic) {
		isPublic = aPublic;
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

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
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

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

}
