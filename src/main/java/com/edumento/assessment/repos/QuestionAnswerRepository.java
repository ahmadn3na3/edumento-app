package com.edumento.assessment.repos;

import com.edumento.assessment.domain.QuestionAnswer;
import com.edumento.core.repos.AbstractMongoRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** Created by ayman on 04/07/16. */
@Repository
public interface QuestionAnswerRepository extends AbstractMongoRepository<QuestionAnswer, String> {

  Optional<QuestionAnswer> findOneByUserIdAndQuestionIdAndDeletedFalse(
      Long userId, Long questionId);
}
