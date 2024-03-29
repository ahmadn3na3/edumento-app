package com.edumento.b2b.repo;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.TimeLock;

@Repository
public interface TimeLockRepository extends JpaRepository<TimeLock, Long> {
  Page<TimeLock> findByFoundationAndDeletedFalse(Foundation foundation, Pageable pageable);

  Page<TimeLock> findByOrganizationAndDeletedFalse(Organization organization, Pageable pageable);

  Stream<TimeLock> findByOrganizationAndDeletedFalse(Organization organization);

  Optional<TimeLock> findOneByNameAndOrganizationAndDeletedFalse(String name,
      Organization organization);
}
