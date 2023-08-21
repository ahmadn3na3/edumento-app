package com.edumento.b2b.repo;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.edumento.b2b.domain.Foundation;
import com.edumento.core.repos.AbstractRepository;

/** Created by ayman on 02/06/16. */
@Repository
public interface FoundationRepository extends AbstractRepository<Foundation, Long> {
  Optional<Foundation> findOneByNameAndDeletedFalse(String instituteName);

  Optional<Foundation> findOneByIdAndDeletedFalse(Long Id);

  Optional<Foundation> findOneByCodeAndDeletedFalse(String code);

  Foundation findByNameAndDeletedFalse(String foundationName);
}
