package com.edumento.b2b.repo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.domain.Organization;

/** Created by ahmad on 3/7/16. */
@Repository
public interface GroupsRepository extends JpaRepository<Groups, Long>, JpaSpecificationExecutor<Groups> {

  Optional<Groups> findOneByNameAndDeletedFalse(String name);

  Optional<Groups> findOneByIdAndDeletedFalse(Long id);

  Stream<Groups> findByOrganizationIdAndDeletedFalse(Long id);

  Stream<Groups> findByFoundationIdAndDeletedFalse(Long id);

  Stream<Groups> findByNameInAndDeletedFalse(List<String> name);

  Stream<Groups> findByIdInAndOrganizationInAndDeletedFalse(Iterable<Long> idss,
      Iterable<Organization> organization);
}
