package com.edumento.discussion.model.comment;

import java.time.ZonedDateTime;
import java.util.List;

/** Created by ayman on 28/08/16. */
public class CommentViewModel {
  private String id;
  private String commentBody;
  private Long userId;
  private String userName;
  private String userImage;
  private String userFullname;
  private Boolean likeEnabled;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModifiedDate;
  private List<LikeViewModel> likes;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCommentBody() {
    return commentBody;
  }

  public void setCommentBody(String commentBody) {
    this.commentBody = commentBody;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getUserImage() {
    return userImage;
  }

  public void setUserImage(String userThumbnail) {
    this.userImage = userThumbnail;
  }

  public Boolean getLikeEnabled() {
    return likeEnabled;
  }

  public void setLikeEnabled(Boolean likeEnabled) {
    this.likeEnabled = likeEnabled;
  }

  public List<LikeViewModel> getLikes() {
    return likes;
  }

  public void setLikes(List<LikeViewModel> likes) {
    this.likes = likes;
  }

  public String getUserFullname() {
    return userFullname;
  }

  public void setUserFullname(String userFullname) {
    this.userFullname = userFullname;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "CommentViewModel{"
        + "id='"
        + id
        + '\''
        + ", commentBody='"
        + commentBody
        + '\''
        + ", userId="
        + userId
        + ", userName='"
        + userName
        + '\''
        + ", userImage='"
        + userImage
        + '\''
        + ", userFullname='"
        + userFullname
        + '\''
        + ", likeEnabled="
        + likeEnabled
        + ", creationDate="
        + creationDate
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", likes="
        + likes
        + '}';
  }
}
