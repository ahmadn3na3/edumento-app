package com.edumento.core.model;

import com.edumento.core.constants.Code;

/** Created by ayman on 13/03/17. */
public class PageResponseModel extends ResponseModel {
  int totalPages;
  int page;
  long size;

  public PageResponseModel(Object data, int totalPages, int page, long size) {
    super(Code.SUCCESS, data);
    this.totalPages = totalPages;
    this.page = page;
    this.size = size;
  }

  public static PageResponseModel done(Object data, int totalPages, int page, long size) {
    return new PageResponseModel(data, totalPages, page, size);
  }

  public int getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(int totalPages) {
    this.totalPages = totalPages;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }
}
