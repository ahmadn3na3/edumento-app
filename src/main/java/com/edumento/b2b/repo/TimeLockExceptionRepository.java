package com.edumento.b2b.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.b2b.domain.TimeLockException;

@Repository
public interface TimeLockExceptionRepository extends JpaRepository<TimeLockException, Long> {
  Optional<TimeLockException> findOneByIdAndTimeLockIdAndDeletedFalse(Long id, Long timeLockId);
}
