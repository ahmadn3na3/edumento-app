package com.edumento.b2b.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.Foundation;

/** Created by ayman on 02/06/16. */
@Repository
public interface FoundationRepository extends JpaRepository<Foundation, Long> {
  Optional<Foundation> findOneByNameAndDeletedFalse(String instituteName);

  Optional<Foundation> findOneByIdAndDeletedFalse(Long Id);

  Optional<Foundation> findOneByCodeAndDeletedFalse(String code);

  Foundation findByNameAndDeletedFalse(String foundationName);
}
