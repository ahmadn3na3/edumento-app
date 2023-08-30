package com.edumento.assessment.repos;

import com.edumento.assessment.domain.AssessmentQuestionChoice;
import com.edumento.core.repos.AbstractRepository;
import org.springframework.stereotype.Repository;

/** Created by ayman on 29/06/16. */
@Repository
public interface AssessmentQuestionChoicesRepository
    extends AbstractRepository<AssessmentQuestionChoice, Long> {}
