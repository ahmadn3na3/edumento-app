package com.edumento.content.models;

import java.time.ZonedDateTime;
import java.util.Date;

import com.edumento.core.constants.ContentStatus;

/** Created by ahmad on 7/3/16. */
public class ContentModel extends ContentCreateModel {
  private Long id;
  private Boolean favorite;
  private Date favoriteDate;
  private String fileName;
  private String folderName;
  private ZonedDateTime lastAccess;
  private Boolean newContent;
  private ContentStatus status;
  private Boolean newAnnotation;
  private Boolean owner = Boolean.FALSE;
  private Integer numberOfViews = 0;
  private Integer numberOfAnnotation = 0;

  private ContentUserModel creator;

  private ZonedDateTime lastModifiedDate;
  private ZonedDateTime creationDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean getFavorite() {
    return favorite;
  }

  public void setFavorite(Boolean favorite) {
    this.favorite = favorite;
  }

  public ZonedDateTime getLastAccess() {
    return lastAccess;
  }

  public void setLastAccess(ZonedDateTime lastAccess) {
    this.lastAccess = lastAccess;
  }

  public Boolean getNewContent() {
    return newContent;
  }

  public void setNewContent(Boolean newContent) {
    this.newContent = newContent;
  }

  public Boolean getNewAnnotation() {
    return newAnnotation;
  }

  public void setNewAnnotation(Boolean newAnnotation) {
    this.newAnnotation = newAnnotation;
  }

  public ContentStatus getStatus() {
    return status;
  }

  public void setStatus(ContentStatus status) {
    this.status = status;
  }

  public ZonedDateTime getLastModifiedDate() {

    return lastModifiedDate;
  }

  public void setLastModifiedDate(ZonedDateTime lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public Boolean getOwner() {
    return owner;
  }

  public void setOwner(Boolean owner) {
    this.owner = owner;
  }

  public ContentUserModel getCreator() {
    return creator;
  }

  public void setCreator(ContentUserModel creator) {
    this.creator = creator;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Date getFavoriteDate() {
    return favoriteDate;
  }

  public void setFavoriteDate(Date favoriteDate) {
    this.favoriteDate = favoriteDate;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public Integer getNumberOfViews() {
    return numberOfViews;
  }

  public void setNumberOfViews(Integer numberOfViews) {
    this.numberOfViews = numberOfViews;
  }

  public Integer getNumberOfAnnotation() {
    return numberOfAnnotation;
  }

  public void setNumberOfAnnotation(Integer numberOfAnnotation) {
    this.numberOfAnnotation = numberOfAnnotation;
  }

  @Override
  public String toString() {
    return "ContentModel{"
        + "id="
        + id
        + ", favorite="
        + favorite
        + ", favoriteDate="
        + favoriteDate
        + ", fileName='"
        + fileName
        + '\''
        + ", lastAccess="
        + lastAccess
        + ", newContent="
        + newContent
        + ", status="
        + status
        + ", newAnnotation="
        + newAnnotation
        + ", owner="
        + owner
        + ", creator="
        + creator
        + ", lastModifiedDate="
        + lastModifiedDate
        + ", creationDate="
        + creationDate
        + "} "
        + super.toString();
  }
}
