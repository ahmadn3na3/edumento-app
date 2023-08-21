package com.edumento.b2b.repo;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import com.edumento.b2b.domain.TimeLockException;
import com.edumento.core.repos.AbstractRepository;

@Repository
public interface TimeLockExceptionRepository extends AbstractRepository<TimeLockException, Long> {
  Optional<TimeLockException> findOneByIdAndTimeLockIdAndDeletedFalse(Long id, Long timeLockId);
}
