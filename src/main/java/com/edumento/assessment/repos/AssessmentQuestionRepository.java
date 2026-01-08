package com.edumento.assessment.repos;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.AssessmentQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Created by ayman on 29/06/16. */
@Repository
public interface AssessmentQuestionRepository
    extends JpaRepository<AssessmentQuestion, Long>, JpaSpecificationExecutor<AssessmentQuestion> {
  List<AssessmentQuestion> findByAssessmentAndDeletedFalse(Assessment assessment);

  Long countByAssessmentIdAndDeletedFalse(Long assessmentId);
}
