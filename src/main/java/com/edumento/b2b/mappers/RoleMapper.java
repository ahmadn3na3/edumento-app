package com.edumento.b2b.mappers;

import com.edumento.b2b.domain.Role;
import com.edumento.b2b.model.role.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {
 RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
 RoleModel roleToRoleModel(Role role);
}
