package com.edumento.discussion.model.discussion;

import java.util.ArrayList;
import java.util.List;

/** Created by ayman on 28/08/16. */
public class DiscussionUpdatesResponseModel {
  List<DiscussionSummaryModel> newDiscussions;
  List<DiscussionSummaryModel> deletedDiscussions;
  List<DiscussionSummaryModel> updatedDiscussions;

  public DiscussionUpdatesResponseModel() {
    newDiscussions = new ArrayList<>();
    deletedDiscussions = new ArrayList<>();
    updatedDiscussions = new ArrayList<>();
  }

  public List<DiscussionSummaryModel> getNewDiscussions() {
    return newDiscussions;
  }

  public void setNewDiscussions(List<DiscussionSummaryModel> newDiscussions) {
    this.newDiscussions = newDiscussions;
  }

  public List<DiscussionSummaryModel> getDeletedDiscussions() {
    return deletedDiscussions;
  }

  public void setDeletedDiscussions(List<DiscussionSummaryModel> deletedDiscussions) {
    this.deletedDiscussions = deletedDiscussions;
  }

  public List<DiscussionSummaryModel> getUpdatedDiscussions() {
    return updatedDiscussions;
  }

  public void setUpdatedDiscussions(List<DiscussionSummaryModel> updatedDiscussions) {
    this.updatedDiscussions = updatedDiscussions;
  }

  @Override
  public String toString() {
    return "DiscussionUpdatesResponseModel{"
        + "newDiscussions="
        + newDiscussions
        + ", deletedDiscussions="
        + deletedDiscussions
        + ", updatedDiscussions="
        + updatedDiscussions
        + '}';
  }
}
