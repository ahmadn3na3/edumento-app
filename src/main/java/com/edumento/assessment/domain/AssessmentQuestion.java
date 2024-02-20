package com.edumento.assessment.domain;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.constants.QuestionType;
import com.edumento.core.constants.ResourceType;
import com.edumento.core.domain.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/** Created by ayman on 29/06/16. */
@Entity
@DynamicInsert
@DynamicUpdate
public class AssessmentQuestion extends AbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "assessment", foreignKey = @ForeignKey(name = "FK_ASSESSMENT"))
	private Assessment assessment;

	@Deprecated
	@Column
	// TODO: remove after migration
	private Long question;

	@Column
	private String body;

	@Column
	private Boolean isPublic = Boolean.TRUE;

	@Enumerated
	private QuestionType questionType;

	@Column(length = 4000)
	private String correctAnswer;

	@Column
	private String bodyResourceUrl;

	@Enumerated(EnumType.STRING)
	private ResourceType resourceType;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "assessmentQuestion", cascade = CascadeType.ALL)
	private Set<AssessmentQuestionChoice> assessmentQuestionChoices = new HashSet<>();

	private Integer questionWeight;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Assessment getAssessment() {
		return assessment;
	}

	public void setAssessment(Assessment assessment) {
		this.assessment = assessment;
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

	public String getBodyResourceUrl() {
		return bodyResourceUrl;
	}

	public void setBodyResourceUrl(String bodyResourceUrl) {
		this.bodyResourceUrl = bodyResourceUrl;
	}

	public Set<AssessmentQuestionChoice> getAssessmentQuestionChoices() {
		return assessmentQuestionChoices;
	}

	public void setAssessmentQuestionChoices(Set<AssessmentQuestionChoice> assessmentQuestionChoices) {
		this.assessmentQuestionChoices = assessmentQuestionChoices;
	}

	public Long getQuestion() {
		return question;
	}

	public void setQuestion(Long question) {
		this.question = question;
	}

	public ResourceType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var that = (AssessmentQuestion) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) {
			return false;
		}
		return getAssessment().equals(that.getAssessment());
	}

	@Override
	public int hashCode() {
		var result = getId() != null ? getId().hashCode() : 0;
		return 31 * result + getAssessment().hashCode();
	}
}
