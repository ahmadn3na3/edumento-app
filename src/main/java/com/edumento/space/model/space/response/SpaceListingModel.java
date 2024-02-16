package com.edumento.space.model.space.response;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.edumento.category.model.CategoryModel;
import com.edumento.core.constants.JoinedStatus;
import com.edumento.core.constants.SpaceRole;

/** Created by ahmad on 3/20/16. */
public class SpaceListingModel {
  private Long id;
  private String color;
  private String image;
  private String thumbnail;
  private String name;
  private String description;

  private CategoryModel categoryModel;

  private SpaceUserModel creator;

  private boolean isPrivate;

  private double rating;
  private boolean newContent;
  private boolean newAssessment;
  private boolean newComments;
  private boolean favorite;
  private boolean owner;
  private int communitySize;
  private int contentSize;
  private boolean joinRequestsAllowed;
  private boolean autoWifiSyncAllowed;
  private boolean showCommunity;
  private boolean allowRecommendation;
  private boolean allowLeave;
  private JoinedStatus joinedStatus = JoinedStatus.NOT_JOINED;
  private List<String> tags;
  private ZonedDateTime creationDate = ZonedDateTime.now();
  private ZonedDateTime lastModified = ZonedDateTime.now();
  private ZonedDateTime lastAccessed = ZonedDateTime.now();
  private List<SpaceUserModel> community = new ArrayList<>();
  private SpaceRole role;
  private Map<String, Byte> permissions = new HashMap<>();

  public SpaceListingModel() {}

  public SpaceListingModel(String url) {
    this.image = url + "/img/default/img.png";
    this.thumbnail = url + "/img/default/bitmap.png";
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isNewContent() {
    return newContent;
  }

  public void setNewContent(boolean newContent) {
    this.newContent = newContent;
  }

  public boolean isNewAssessment() {
    return newAssessment;
  }

  public void setNewAssessment(boolean newAssessment) {
    this.newAssessment = newAssessment;
  }

  public boolean isNewComments() {
    return newComments;
  }

  public void setNewComments(boolean newComments) {
    this.newComments = newComments;
  }

  public boolean isFavorite() {
    return favorite;
  }

  public void setFavorite(boolean favorite) {
    this.favorite = favorite;
  }

  public double getRating() {
    return rating;
  }

  public void setRating(double rate) {
    this.rating = rate;
  }

  public SpaceUserModel getCreator() {
    return creator;
  }

  public void setCreator(SpaceUserModel creator) {
    this.creator = creator;
  }

  public boolean isOwner() {
    return owner;
  }

  public void setOwner(boolean owner) {
    this.owner = owner;
  }

  public List<SpaceUserModel> getCommunity() {
    return community;
  }

  public void setCommunity(List<SpaceUserModel> community) {
    this.community = community;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    if (null == image) {
      return;
    }
    this.image = image;
  }

  public int getCommunitySize() {
    return communitySize;
  }

  public void setCommunitySize(int communitySize) {
    this.communitySize = communitySize;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public ZonedDateTime getLastModified() {
    return lastModified;
  }

  public void setLastModified(ZonedDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    if (thumbnail == null) {
      return;
    }
    this.thumbnail = thumbnail;
  }

  public CategoryModel getCategoryModel() {
    return categoryModel;
  }

  public void setCategoryModel(CategoryModel categoryModel) {
    this.categoryModel = categoryModel;
  }

  public boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public boolean isJoinRequestsAllowed() {
    return joinRequestsAllowed;
  }

  public void setJoinRequestsAllowed(boolean joinRequestsAllowed) {
    this.joinRequestsAllowed = joinRequestsAllowed;
  }

  public boolean isAutoWifiSyncAllowed() {
    return autoWifiSyncAllowed;
  }

  public void setAutoWifiSyncAllowed(boolean autoWifiSyncAllowed) {
    this.autoWifiSyncAllowed = autoWifiSyncAllowed;
  }

  public boolean isShowCommunity() {
    return showCommunity;
  }

  public void setShowCommunity(boolean showCommunity) {
    this.showCommunity = showCommunity;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, Byte> getPermissions() {
    return permissions;
  }

  public void setPermissions(Map<String, Byte> permissions) {
    this.permissions = permissions;
  }

  public boolean isAllowRecommendation() {
    return allowRecommendation;
  }

  public void setAllowRecommendation(boolean allowRecommendation) {
    this.allowRecommendation = allowRecommendation;
  }

  public ZonedDateTime getLastAccessed() {
    return lastAccessed;
  }

  public void setLastAccessed(ZonedDateTime lastAccessed) {
    this.lastAccessed = lastAccessed;
  }

  public int getContentSize() {
    return contentSize;
  }

  public void setContentSize(int contentSize) {
    this.contentSize = contentSize;
  }

  public SpaceRole getRole() {
    return role;
  }

  public void setRole(SpaceRole role) {
    this.role = role;
  }

  public boolean isAllowLeave() {
    return allowLeave;
  }

  public void setAllowLeave(boolean allowLeave) {
    this.allowLeave = allowLeave;
  }

  public JoinedStatus getJoinedStatus() {
    return joinedStatus;
  }

  public void setJoinedStatus(JoinedStatus joinedStatus) {
    this.joinedStatus = joinedStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    SpaceListingModel that = (SpaceListingModel) o;

    return getId().equals(that.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public String toString() {
    return "SpaceListingModel{" + "id=" + id +
            ", color='" + color + '\'' + ", image='" + image +
            '\'' + ", thumbnail='" + thumbnail + '\'' + ", name='" +
            name + '\'' + ", description='" + description + '\'' +
            ", categoryModel=" + categoryModel + ", creator=" + creator +
            ", isPrivate=" + isPrivate + ", rating=" + rating +
            ", newContent=" + newContent + ", newAssessment=" + newAssessment +
            ", newComments=" + newComments + ", favorite=" + favorite +
            ", owner=" + owner + ", communitySize=" + communitySize +
            ", contentSize=" + contentSize + ", joinRequestsAllowed=" +
            joinRequestsAllowed + ", autoWifiSyncAllowed=" + autoWifiSyncAllowed +
            ", showCommunity=" + showCommunity + ", allowRecommendation=" +
            allowRecommendation + ", tags=" + tags + ", creationDate=" +
            creationDate + ", lastModified=" + lastModified +
            ", lastAccessed=" + lastAccessed + ", community=" + community +
            ", permissions=" + permissions + '}';
  }
}
