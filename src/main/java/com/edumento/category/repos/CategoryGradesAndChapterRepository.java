package com.edumento.category.repos;

import org.springframework.stereotype.Repository;
import com.edumento.category.domain.CategoryGradesAndChapter;
import com.edumento.core.repos.AbstractMongoRepository;

/** Created by ahmad on 5/15/17. */
@Repository
public interface CategoryGradesAndChapterRepository
    extends AbstractMongoRepository<CategoryGradesAndChapter, String> {
  CategoryGradesAndChapter findByCategoryId(Long category);
}
