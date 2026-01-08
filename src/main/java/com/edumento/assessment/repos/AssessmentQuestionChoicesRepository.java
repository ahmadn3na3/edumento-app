package com.edumento.assessment.repos;

import com.edumento.assessment.domain.AssessmentQuestionChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Created by ayman on 29/06/16. */
@Repository
public interface AssessmentQuestionChoicesRepository
    extends JpaRepository<AssessmentQuestionChoice, Long>,
        JpaSpecificationExecutor<AssessmentQuestionChoice> {

  AssessmentQuestionChoice findByAssessmentQuestionIdAndId(
      Long assessmentQuestionId, Long assessmentQuestionChoiceId);
}
