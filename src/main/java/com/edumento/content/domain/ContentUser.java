package com.edumento.content.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.edumento.core.domain.AbstractEntity;
import com.edumento.core.model.TimeSpentModel;

/** Created by ahmad on 7/20/16. */
@CompoundIndex(name = "user_conent_idx", def = "{userName : 1, contentId : 1}", unique = true)
@Document(collection = "content_user")
public class ContentUser extends AbstractEntity {
  @Id
  private String id;
  @Field
  private String userName;
  @Field
  private Long userId;
  @Field
  private Long contentId;
  @Field
  private Boolean favorite = Boolean.FALSE;
  @Field
  private Date favoriteDate;
  @Field
  private Date lastAccessDate;
  @Field
  private Integer views = 0;

  private List<TimeSpentModel> timeSpent = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  public Boolean getFavorite() {
    return favorite;
  }

  public void setFavorite(Boolean favorite) {
    this.favorite = favorite;
  }

  public Date getFavoriteDate() {
    return favoriteDate;
  }

  public void setFavoriteDate(Date favoriteDate) {
    this.favoriteDate = favoriteDate;
  }

  public Date getLastAccessDate() {
    return lastAccessDate;
  }

  public void setLastAccessDate(Date lastAccessDate) {
    this.lastAccessDate = lastAccessDate;
  }

  public List<TimeSpentModel> getTimeSpent() {
    return timeSpent;
  }

  public void setTimeSpent(List<TimeSpentModel> timeSpent) {
    this.timeSpent = timeSpent;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Integer getViews() {
    return views;
  }

  public void setViews(Integer views) {
    this.views = views;
  }
}
