package com.edumento.assessment.repos;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.assessment.domain.UserAssessment;

/** Created by ayman on 04/07/16. */
@Repository
public interface UserAssessmentRepository extends MongoRepository<UserAssessment, String> {
  Stream<UserAssessment> findByAssessmentIdAndDeletedFalse(Long id);

  Stream<UserAssessment> findByAssessmentIdAndDeletedFalseOrderByTotalGradeAsc(Long id);

  Stream<UserAssessment> findByAssessmentIdAndDeletedFalseOrderByTotalGradeDesc(Long id);

  Optional<UserAssessment> findOneByUserIdAndAssessmentIdAndDeletedFalse(
      Long userId, Long assessmentId);

  Integer countByAssessmentId(Long id);
}
