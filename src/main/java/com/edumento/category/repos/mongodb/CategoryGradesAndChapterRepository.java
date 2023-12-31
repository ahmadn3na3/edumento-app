package com.edumento.category.repos.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.edumento.category.domain.CategoryGradesAndChapter;

/** Created by ahmad on 5/15/17. */
@Repository
public interface CategoryGradesAndChapterRepository
    extends MongoRepository<CategoryGradesAndChapter, String> {
  CategoryGradesAndChapter findByCategoryId(Long category);
}
