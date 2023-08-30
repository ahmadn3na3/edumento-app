package com.edumento.content.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.edumento.content.domain.Content;
import com.edumento.content.models.ContentModel;

@Mapper
public interface ContentMapper {
	ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "owner", ignore = true)
	ContentModel mapContentToContentModel(Content content);
}
