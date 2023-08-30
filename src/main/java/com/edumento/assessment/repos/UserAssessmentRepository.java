package com.edumento.assessment.repos;

import com.edumento.assessment.domain.UserAssessment;
import com.edumento.core.repos.AbstractMongoRepository;
import java.util.Optional;
import java.util.stream.Stream;

/** Created by ayman on 04/07/16. */
public interface UserAssessmentRepository extends AbstractMongoRepository<UserAssessment, String> {
  Stream<UserAssessment> findByAssessmentIdAndDeletedFalse(Long id);

  Stream<UserAssessment> findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(Long id);

  Stream<UserAssessment> findByAssessmentIdAndDeletedFalseOrderByTotalGradeDesc(Long id);

  Optional<UserAssessment> findOneByUserIdAndAssessmentIdAndDeletedFalse(
      Long userId, Long assessmentId);

  Integer countByAssessmentId(Long id);
}
