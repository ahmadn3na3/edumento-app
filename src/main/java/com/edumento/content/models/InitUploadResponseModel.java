package com.edumento.content.models;

import java.util.List;

import com.edumento.content.domain.Chunk;

public class InitUploadResponseModel {
  private String taskId;
  private List<Chunk> chunkList;

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public List<Chunk> getChunkList() {
    return chunkList;
  }

  public void setChunkList(List<Chunk> chunkList) {
    this.chunkList = chunkList;
  }
}
