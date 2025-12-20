package com.edumento.category.repos;

import java.util.Optional;
import java.util.stream.Stream;

import com.edumento.category.domain.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

	Optional<Category> findOneByNameAndDeletedFalse(String name);

	Stream<Category> findByDeletedFalse();

	Optional<Category> findOneByIdAndDeletedFalse(Long id);

	@Query("select j.space.category from Joined j where j.user.id = ?1 and j.deleted=false and j.space.deleted=false and j.space.category.deleted=false")
	Stream<Category> findRelatedCategoryWithSpaceForUserAndDeletedFalse(Long userId);

}
