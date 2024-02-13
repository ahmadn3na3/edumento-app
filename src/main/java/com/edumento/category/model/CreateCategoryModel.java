package com.edumento.category.model;

import java.util.LinkedList;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 3/13/16. */
public class CreateCategoryModel {
  @NotNull(message = "error.label.name.null")
  private String name;

  private String nameAr;
  private String color;
  private String image;
  private String thumbnail;
  private Long organizationId;
  private Long foundationId;
  private Long parentId;
  private LinkedList<Chapter> chapters = new LinkedList<>();
  private LinkedList<Grade> grades = new LinkedList<>();

  public CreateCategoryModel() {}

  public CreateCategoryModel(String name, String color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public LinkedList<Chapter> getChapters() {
    return chapters;
  }

  public void setChapters(LinkedList<Chapter> chapters) {
    this.chapters = chapters;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public Long getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(Long organizationId) {
    this.organizationId = organizationId;
  }

  public Long getFoundationId() {
    return foundationId;
  }

  public void setFoundationId(Long foundationId) {
    this.foundationId = foundationId;
  }

  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  public LinkedList<Grade> getGrades() {
    return grades;
  }

  public void setGrades(LinkedList<Grade> grades) {
    this.grades = grades;
  }

  public String getNameAr() {
    return nameAr;
  }

  public void setNameAr(String nameAr) {
    this.nameAr = nameAr;
  }

  @Override
  public String toString() {
    return "CreateCategoryModel{"
        + "name='"
        + name
        + '\''
        + ", color='"
        + color
        + '\''
        + ", image='"
        + image
        + '\''
        + ", thumbnail='"
        + thumbnail
        + '\''
        + ", organizationId="
        + organizationId
        + ", foundationId="
        + foundationId
        + ", chapters="
        + chapters
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CreateCategoryModel that = (CreateCategoryModel) o;

    return name.equals(that.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}
