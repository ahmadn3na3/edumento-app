package com.edumento.b2b.repo;

import org.springframework.stereotype.Repository;
import com.edumento.b2b.domain.FoundationPackage;
import com.edumento.core.repos.AbstractRepository;

/** Created by ahmad on 4/16/17. */
@Repository
public interface FoundationPackageRepository extends AbstractRepository<FoundationPackage, Long> {
  FoundationPackage findByNameAndDeletedFalse(String name);
}
