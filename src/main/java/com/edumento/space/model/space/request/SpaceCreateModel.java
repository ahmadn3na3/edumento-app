package com.edumento.space.model.space.request;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.edumento.category.model.CategoryModel;
import com.edumento.core.util.RandomUtils;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/2/16. */
public class SpaceCreateModel {

  private String color = RandomUtils.genertateRandomColor();

  @NotNull(message = "error.space.name.null")
  private String name;

  private List<String> tags = new ArrayList<>();
  private Double price;
  private Boolean paid = Boolean.FALSE;
  private Boolean isPrivate = Boolean.FALSE;
  private String image;
  private String thumbnail;
  private String description;

  @NotNull(message = "error.space.label.null")
  private CategoryModel categoryModel;

  private Boolean joinRequestsAllowed = Boolean.FALSE;
  private Boolean autoWifiSyncAllowed = Boolean.FALSE;
  private Boolean showCommunity = Boolean.FALSE;
  private Boolean allowRecommendation = Boolean.FALSE;
  private Boolean allowLeave = Boolean.TRUE;
  private Long ownerId;
  private ZonedDateTime creationDate;

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

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public CategoryModel getCategoryModel() {
    return categoryModel;
  }

  public void setCategoryModel(CategoryModel categoryModel) {
    this.categoryModel = categoryModel;
  }

  public Boolean getPaid() {
    return paid;
  }

  public void setPaid(Boolean paid) {
    this.paid = paid;
  }

  public Boolean getJoinRequestsAllowed() {
    return joinRequestsAllowed;
  }

  public void setJoinRequestsAllowed(Boolean joinRequestsAllowed) {
    this.joinRequestsAllowed = joinRequestsAllowed;
  }

  public Boolean getAutoWifiSyncAllowed() {
    return autoWifiSyncAllowed;
  }

  public void setAutoWifiSyncAllowed(Boolean autoWifiSyncAllowed) {
    this.autoWifiSyncAllowed = autoWifiSyncAllowed;
  }

  public Boolean getShowCommunity() {
    return showCommunity;
  }

  public void setShowCommunity(Boolean showCommunity) {
    this.showCommunity = showCommunity;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Boolean getAllowRecommendation() {
    return allowRecommendation;
  }

  public void setAllowRecommendation(Boolean allowRecommendation) {
    this.allowRecommendation = allowRecommendation;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  public ZonedDateTime getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(ZonedDateTime creationDate) {
    this.creationDate = creationDate;
  }

  public Boolean getAllowLeave() {
    return allowLeave;
  }

  public void setAllowLeave(Boolean allowLeave) {
    this.allowLeave = allowLeave;
  }

  @Override
  public String toString() {
    return String.format(
        "SpaceCreateModel{color='%s', name='%s', tags=%s, price=%s, paid=%s, isPrivate=%s, image='%s', thumbnail='%s', description='%s', categoryModel=%s, joinRequestsAllowed=%s, autoWifiSyncAllowed=%s, showCommunity=%s, allowRecommendation=%s, ownerId=%d, creationDate=%s}",
        color, name, tags, price, paid, isPrivate, image, thumbnail, description, categoryModel,
        joinRequestsAllowed, autoWifiSyncAllowed, showCommunity, allowRecommendation, ownerId,
        creationDate);
  }
}
