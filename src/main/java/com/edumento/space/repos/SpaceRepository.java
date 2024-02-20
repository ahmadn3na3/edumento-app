package com.edumento.space.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.category.domain.Category;
import com.edumento.space.domain.Space;

/** Created by ahmad on 3/2/16. */
@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

	Integer countByNameAndUserIdAndCategoryAndDeletedFalse(String name, Long userId, Category category);

	Integer countByNameAndUserIdAndDeletedFalse(String name, Long userId);

	@Query("""
			Select s from Space s Where ((s.name like CONCAT('%',?1,'%'))\s\
			or (s.description like CONCAT('%',?1,'%'))\s\
			or (s.objective like CONCAT('%',?1,'%')))\s\
			and s.isPrivate=false and s.deleted=false\s\
			and s.category.organization is null and s.category.foundation is null\s""")
	Page<Space> searchForSpace(String name, Pageable pagingModel);

	Stream<Space> findByIsPrivateFalseAndObjectiveContains(String tag);

	Stream<Space> findByUserIdAndDeletedFalseAndCreationDateAfter(Long userId, Date Date);

	Stream<Space> findByCategoryOrganizationInAndDeletedFalse(List<Organization> organization);

	Page<Space> findByCategoryOrganizationInAndDeletedFalse(List<Organization> organization, Pageable pageable);

	Stream<Space> findByCategoryFoundationAndDeletedFalse(Foundation foundation);

	Page<Space> findByCategoryOrganizationIsNullAndCategoryFoundationIsNullAndDeletedFalse(Pageable pageable);

	Optional<Space> findOneByIdAndDeletedFalse(Long id);

	Optional<Space> findOneByIdAndDeletedTrue(Long id);

	@Query("update Space s set  s.lastModifiedDate = current_timestamp() where s = ?1")
	@Modifying
	void updateSpaceModificationDate(Space space);

	Stream<Space> findByCategoryAndDeletedFalse(Category category);

	Integer countByCategoryAndDeletedFalse(Category category);

	@Query(value = "select count(s) from Space s where s.user.id=?1")
	Integer countByOwnerId(Long userId);

	Stream<Space> findByDeletedFalse();
}
