package com.edumento.discussion.model.discussion;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.edumento.core.constants.DiscussionType;


/** Created by ayman on 25/08/16. */
public class DiscussionCreateModel {

  private String title;
  private String body;
  private String resourceUrl;
  private Long spaceId;
  private Long contentId;
  private DiscussionType type;

  public DiscussionCreateModel() {
  }

  /**
   * @return the type
   */
  public DiscussionType getType() {
    if (this.type == null) {
		return DiscussionType.DISCUSSION;
	}
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(DiscussionType type) {
    this.type = type;
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

  public Long getSpaceId() {
    return spaceId;
  }

  public void setSpaceId(Long spaceId) {
    this.spaceId = spaceId;
  }

  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).toString();
  }
}
