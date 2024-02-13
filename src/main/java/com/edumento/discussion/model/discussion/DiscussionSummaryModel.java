package com.edumento.discussion.model.discussion;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.edumento.core.constants.DiscussionType;



/** Created by ayman on 25/08/16. */
public class DiscussionSummaryModel {
  private String id;
  private String title;
  private String body;
  private String resourceUrl;
  private String ownerThumb;
  private String ownerName;
  private Long ownerId;
  private Integer commentsCounter;
  private ZonedDateTime creationDate;
  private Long contentId;
  private DiscussionType type;
  private Long spaceId;

  public DiscussionSummaryModel() {
  }

  /**
   * @return the type
   */
  public DiscussionType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(DiscussionType type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getResourceUrl() {
    return resourceUrl;
  }

  public void setResourceUrl(String resourceUrl) {
    this.resourceUrl = resourceUrl;
  }

  public String getOwnerThumb() {
    return ownerThumb;
  }

  public void setOwnerThumb(String ownerThumb) {
    this.ownerThumb = ownerThumb;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public Integer getCommentsCounter() {
    return commentsCounter;
  }

  public void setCommentsCounter(Integer commentsCounter) {
    this.commentsCounter = commentsCounter;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

public Long getSpaceId() {
	return spaceId;
}

public void setSpaceId(Long spaceId) {
	this.spaceId = spaceId;
}
}
