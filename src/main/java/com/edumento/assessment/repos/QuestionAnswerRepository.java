package com.edumento.assessment.repos;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.assessment.domain.QuestionAnswer;

/** Created by ayman on 04/07/16. */
@Repository
public interface QuestionAnswerRepository extends MongoRepository<QuestionAnswer, String> {

	Optional<QuestionAnswer> findOneByUserIdAndQuestionIdAndDeletedFalse(Long userId, Long questionId);
}
