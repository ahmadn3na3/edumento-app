package com.edumento.space.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.edumento.space.domain.Space;
import com.edumento.space.model.space.response.SpaceListingModel;
import com.edumento.space.model.space.response.SpaceUserModel;
import com.edumento.user.domain.User;

@Mapper
public interface SpaceMapper {
    SpaceMapper INSTANCE = Mappers.getMapper(SpaceMapper.class);

    SpaceUserModel userToSpaceUserModel(User user);

    void mapSpaceDomainToListingModel(Space space, @MappingTarget SpaceListingModel spaceListingModel);
}
