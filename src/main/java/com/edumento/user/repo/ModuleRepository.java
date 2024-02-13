package com.edumento.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.user.domain.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
	Module findOneByKeyCode(String keyCode);

}
