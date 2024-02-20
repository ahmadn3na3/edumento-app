package com.edumento.discussion.model.discussion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/** Created by ayman on 25/08/16. */
public class CommentCreateModel {

	@NotNull(message = "error.comment.body")
	@NotEmpty(message = "error.comment.body")
	private String commentBody;

	public String getCommentBody() {
		return commentBody;
	}

	public void setCommentBody(String commentBody) {
		this.commentBody = commentBody;
	}
}
