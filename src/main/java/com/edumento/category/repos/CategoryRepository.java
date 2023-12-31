package com.edumento.category.repos;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.category.domain.Category;

/** Created by ahmad on 3/13/16. */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Optional<Category> findOneByNameAndOrganizationAndDeletedFalse(
            String name, Organization organization);

    Optional<Category> findOneByNameAndFoundationAndOrganizationIsNullAndDeletedFalse(
            String name, Foundation foundation);

    Optional<Category> findOneByNameAndOrganizationIsNullAndFoundationIsNullAndDeletedFalse(
            String name);

    Stream<Category> findByOrganizationIsNullAndFoundationIsNullAndDeletedFalse();

    Stream<Category> findByFoundationAndDeletedFalse(Foundation foundation);

    Stream<Category> findByOrganizationAndDeletedFalse(Organization organization);

    Optional<Category> findOneByIdAndDeletedFalse(Long id);

    @Query("select j.space.category from Joined j where j.user.id = ?1 and j.deleted=false and j.space.deleted=false and j.space.category.deleted=false")
    Stream<Category> findRelatedCategoryWithSpaceForUserAndDeletedFalse(Long userId);

}
