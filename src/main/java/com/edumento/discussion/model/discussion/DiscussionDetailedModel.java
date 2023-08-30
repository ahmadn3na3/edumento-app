package com.edumento.discussion.model.discussion;

import com.edumento.discussion.model.comment.CommentViewModel;

import java.util.List;

/** Created by ayman on 28/08/16. */
public class DiscussionDetailedModel extends DiscussionSummaryModel {

  private List<CommentViewModel> comments;

  public List<CommentViewModel> getComments() {
    return comments;
  }

  public void setComments(List<CommentViewModel> comments) {
    this.comments = comments;
  }

  @Override
  public String toString() {
    return "DiscussionDetailedModel{" + "comments=" + comments + "} " + super.toString();
  }
}
