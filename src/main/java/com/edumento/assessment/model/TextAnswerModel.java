package com.edumento.assessment.model;

/** Created by ahmad on 11/27/16. */
public class TextAnswerModel {
  private String textBody;
  private Integer pageNumber;

  private Double startX;
  private Double startY;
  private Double endX;
  private Double endY;
  private Double density;
  private Double startIndex;
  private Double endIndex;

  public String getTextBody() {
    return textBody;
  }

  public void setTextBody(String textBody) {
    this.textBody = textBody;
  }

  public Double getStartIndex() {
    return startIndex;
  }

  public void setStartIndex(Double startIndex) {
    this.startIndex = startIndex;
  }

  public Double getEndIndex() {
    return endIndex;
  }

  public void setEndIndex(Double endIndex) {
    this.endIndex = endIndex;
  }

  public Integer getPageNumber() {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber) {
    this.pageNumber = pageNumber;
  }

  public Double getStartX() {
    return startX;
  }

  public void setStartX(Double startX) {
    this.startX = startX;
  }

  public Double getStartY() {
    return startY;
  }

  public void setStartY(Double startY) {
    this.startY = startY;
  }

  public Double getEndX() {
    return endX;
  }

  public void setEndX(Double endX) {
    this.endX = endX;
  }

  public Double getEndY() {
    return endY;
  }

  public void setEndY(Double endY) {
    this.endY = endY;
  }

  public Double getDensity() {
    return density;
  }

  public void setDensity(Double density) {
    this.density = density;
  }

  @Override
  public String toString() {
    return "TextAnswerModel{"
        + "textBody='"
        + textBody
        + '\''
        + ", pageNumber="
        + pageNumber
        + ", startX="
        + startX
        + ", startY="
        + startY
        + ", endX="
        + endX
        + ", endY="
        + endY
        + ", density="
        + density
        + ", startIndex="
        + startIndex
        + ", endIndex="
        + endIndex
        + '}';
  }
}
