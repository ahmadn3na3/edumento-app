package com.edumento.b2b.repo;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;

/** Created by ahmad on 2/29/16. */
@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

  Optional<Organization> findOneByNameAndDeletedFalse(String organizationName);

  Optional<Organization> findOneByIdAndDeletedFalse(Long Id);

  Optional<Organization> findOneByOrgIdAndDeletedFalse(String orgId);

  Stream<Organization> findByFoundationIdAndDeletedFalse(Long id);

  Page<Organization> findByFoundationIdAndDeletedFalse(Long id, Pageable pageable);

  Integer countByFoundationAndDeletedFalse(Foundation foundation);
}
