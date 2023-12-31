package com.edumento.b2b.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.FoundationPackage;

/** Created by ahmad on 4/16/17. */
@Repository
public interface FoundationPackageRepository extends JpaRepository<FoundationPackage, Long> {
  FoundationPackage findByNameAndDeletedFalse(String name);
}
