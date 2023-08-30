package com.edumento.assessment.repos;

import com.edumento.assessment.domain.Assessment;
import com.edumento.assessment.domain.AssessmentQuestion;
import com.edumento.core.repos.AbstractRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

/** Created by ayman on 29/06/16. */
@Repository
public interface AssessmentQuestionRepository extends AbstractRepository<AssessmentQuestion, Long> {
  List<AssessmentQuestion> findByAssessmentAndDeletedFalse(Assessment assessment);

  Long countByAssessmentIdAndDeletedFalse(Long assessmentId);
}
