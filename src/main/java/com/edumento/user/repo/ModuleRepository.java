package com.edumento.user.repo;

import com.edumento.user.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
  Module findOneByKeyCode(String keyCode);
}
