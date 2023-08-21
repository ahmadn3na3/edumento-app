package com.edumento.b2b.repo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.core.repos.AbstractRepository;

/** Created by ahmad on 3/23/16. */
@Repository
public interface RoleRepository
    extends AbstractRepository<Role, Long>, JpaSpecificationExecutor<Role> {

  Optional<Role> findOneByIdAndDeletedFalse(Long id);

  Optional<List<Role>> findByUsersIdAndDeletedFalse(Long id);

  Stream<Role> findByOrganizationAndDeletedFalse(Organization organization);

  Stream<Role> findByFoundationAndDeletedFalse(Foundation foundation);

  Optional<Role> findOneByOrganizationInAndIdAndDeletedFalse(Iterable<Organization> organization,
      Long id);

  Optional<Role> findOneByFoundationAndIdAndDeletedFalse(Foundation foundation, Long id);

  Optional<Role> findOneByNameAndOrganizationAndDeletedFalse(String s, Organization organization);

  Optional<Role> findOneByNameAndFoundationAndDeletedFalse(String s, Foundation organization);
}
