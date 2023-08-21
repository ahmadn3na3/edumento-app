package com.edumento.category.model;

/** Created by ahmad on 6/20/17. */
public class LoadCategoryModel extends CreateCategoryModel {
  private String nameAr;

  @Override
  public String getNameAr() {
    return nameAr;
  }

  @Override
  public void setNameAr(String nameAr) {
    this.nameAr = nameAr;
  }
}
