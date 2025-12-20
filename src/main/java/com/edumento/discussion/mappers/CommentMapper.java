package com.edumento.discussion.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.edumento.discussion.domain.Comment;
import com.edumento.discussion.model.comment.CommentViewModel;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

	@Mapping(target = "commentBody", source = "body")
	@Mapping(target = "userImage", source = "userThumbnail")
	@Mapping(target = "userFullname", source = "userFullName")
	CommentViewModel mapComment(Comment comment);

	List<CommentViewModel> mapComments(List<Comment> comments);
}
