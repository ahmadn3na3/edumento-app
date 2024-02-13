package com.edumento.content.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.content.services.ContentViewService;
import com.edumento.core.model.ResponseModel;

@RestController
@RequestMapping("/api/content/view")
public class ContentViewController {
  @Autowired
  ContentViewService contentViewService;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  // @ApiOperation(
  // value = "get content url",
  // response = ContentViewModel.class,
  // notes = "this method is used to view content"
  // )
  public ResponseModel viewContent(
      @PathVariable Long id, @RequestHeader(name = "original", required = false) boolean getOrginal)
      throws IOException {
    return contentViewService.getContentServUrl(id, false, getOrginal);
  }

  @RequestMapping(path = "/{id}/android", method = RequestMethod.GET)
  // @ApiOperation(
  // value = "get content url for android",
  // response = ContentViewModel.class,
  // notes = "this method is used to get content url for android"
  // )
  public ResponseModel android(
      @PathVariable Long id, @RequestHeader(name = "original", required = false) boolean getOrginal)
      throws IOException {
    return contentViewService.getContentServUrl(id, true, getOrginal);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  // @ApiOperation(
  // value = "view contnet",
  // notes = "this method is used to finish view content by task id"
  // )
  public ResponseModel viewContent(@PathVariable String id) throws IOException {
    return contentViewService.finishView(id);
  }
}
