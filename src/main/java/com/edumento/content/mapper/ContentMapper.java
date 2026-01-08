package com.edumento.content.mapper;

import com.edumento.content.domain.Content;
import com.edumento.content.models.ContentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContentMapper {
  ContentMapper INSTANCE = Mappers.getMapper(ContentMapper.class);

  @Mapping(target = "tags", ignore = true)
  @Mapping(target = "owner", ignore = true)
  ContentModel mapContentToContentModel(Content content);
}
