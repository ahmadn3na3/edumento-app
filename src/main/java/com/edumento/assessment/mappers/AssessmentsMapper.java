package com.edumento.assessment.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.AssessmentQuestion;
import com.edumento.assessment.domain.QuestionAnswer;
import com.edumento.assessment.model.AssessmentCreateModel;
import com.edumento.assessment.model.AssessmentQuestionCreateModel;
import com.edumento.assessment.model.QuestionAnswerModel;
import com.edumento.assessment.model.WorkSheetAnswerModel;

@Mapper
public interface AssessmentsMapper {
	AssessmentsMapper INSTANCE = Mappers.getMapper(AssessmentsMapper.class);

	void createModelToEntity(AssessmentCreateModel assesmentCreateModel, @MappingTarget Assessment assessment);

	void mapAssessmentQuestionModelToDomain(AssessmentQuestionCreateModel assessmentQuestionCreateModel,
			@MappingTarget AssessmentQuestion assessmentQuestion);

	WorkSheetAnswerModel cloneToNewModel(WorkSheetAnswerModel userWorkSheetAnswerModel);

	void mapQuestionAnswerModelToDomain(QuestionAnswerModel questionAnswerModel,
			@MappingTarget QuestionAnswer questionAnswer);
}
