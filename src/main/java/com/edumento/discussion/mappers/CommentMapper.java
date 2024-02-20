package com.edumento.discussion.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.edumento.discussion.domain.Comment;
import com.edumento.discussion.model.comment.CommentViewModel;

@Mapper
public interface CommentMapper {
	CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

	List<CommentViewModel> mapComments(List<Comment> comments);
}
