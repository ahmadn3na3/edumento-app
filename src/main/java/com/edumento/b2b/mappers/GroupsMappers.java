package com.edumento.b2b.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.edumento.b2b.domain.Groups;
import com.edumento.b2b.model.group.GroupModel;

@Mapper
public interface GroupsMappers {
	GroupsMappers INSTANCE = Mappers.getMapper(GroupsMappers.class);

	@Mapping(target = "canAccess", ignore = true)
	@Mapping(target = "tags", ignore = true)
	GroupModel groupsToGroupModel(Groups groups);
}
