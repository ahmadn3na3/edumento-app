package com.edumento.content.models;

import jakarta.validation.constraints.NotNull;

public class InitUploadModel {
  @NotNull private Long contentId;
  private Integer chunkSize = -1;

  public InitUploadModel() {}

  public InitUploadModel(Long contentId) {
    this.contentId = contentId;
  }

  public Long getContentId() {
    return contentId;
  }

  public void setContentId(Long contentId) {
    this.contentId = contentId;
  }

  public Integer getChunkSize() {
    return chunkSize;
  }

  public void setChunkSize(Integer chunkSize) {
    this.chunkSize = chunkSize;
  }
}
