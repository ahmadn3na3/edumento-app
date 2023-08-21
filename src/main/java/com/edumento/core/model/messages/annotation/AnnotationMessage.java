package com.edumento.core.model.messages.annotation;

import org.apache.commons.lang3.builder.ToStringBuilder;
import com.edumento.core.model.messages.From;

public class AnnotationMessage {
  private String id;
  private Long contentId;
  private String contentName;
  private Long spaceId;
  private String spaceName;
  private String categoryName;
  private From from;

  public AnnotationMessage() {}

  public AnnotationMessage(
      String id,
      Long contentId,
      String contentName,
      Long spaceId,
      String spaceName,
      String categoryName,
      From from) {
    this.id = id;
    this.contentId = contentId;
    this.contentName = contentName;
    this.spaceId = spaceId;
    this.spaceName = spaceName;
    this.categoryName = categoryName;
    this.from = from;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  public String getContentName() {
    return contentName;
  }

  public void setContentName(String contentName) {
    this.contentName = contentName;
  }

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public String getSpaceName() {
    return spaceName;
  }

  public void setSpaceName(String spaceName) {
    this.spaceName = spaceName;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public void setCategoryName(String categoryName) {
    this.categoryName = categoryName;
  }

  public From getFrom() {
    return from;
  }

  public void setFrom(From from) {
    this.from = from;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", id)
        .append("contentId", contentId)
        .append("contentName", contentName)
        .append("spaceId", spaceId)
        .append("spaceName", spaceName)
        .append("categoryName", categoryName)
        .append("from", from)
        .toString();
  }
}
