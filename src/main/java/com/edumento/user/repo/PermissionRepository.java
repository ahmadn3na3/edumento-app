package com.edumento.user.repo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edumento.user.constant.UserType;
import com.edumento.user.domain.Module;
import com.edumento.user.domain.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

	List<Permission> findByTypeInAndDeletedFalse(Collection<UserType> types);

	Stream<Permission> findByModuleInAndDeletedFalse(Collection<Module> modules);

	Permission findByName(String name);

}
