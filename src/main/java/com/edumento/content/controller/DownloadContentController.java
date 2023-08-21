package com.edumento.content.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.content.services.DownloadService;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Created by ahmad on 7/10/16. */
@RestController
@RequestMapping("/api/content/download")
public class DownloadContentController {

  @Autowired
  DownloadService downloadService;

  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  // @ApiOperation(
  // value = "Initiate Download",
  // notes = "this method is used to Initiate download process by content id "
  // )
  public ResponseModel init(@PathVariable Long id, HttpServletRequest request) {
    return downloadService.startDownload(id, request);
  }

  @RequestMapping(method = RequestMethod.GET)
  // @ApiOperation(value = "Resume Download", notes = "this method is used to
  // resume download task")
  public void resume(HttpServletRequest request, HttpServletResponse response) throws IOException {
    downloadService.getRange(request, response);
  }

  @RequestMapping(path = "/commit/{id}", method = RequestMethod.GET)
  // @ApiOperation(value = "Commit", notes = "this method is used to commit
  // download task")
  public ResponseModel startUpload(@PathVariable String id, HttpServletRequest request) {
    return downloadService.commit(id, request);
  }

  @RequestMapping(path = "/cancel/{id}", method = RequestMethod.GET)
  // @ApiOperation(value = "cancel task", notes = "this method is used to cancel
  // download task")
  public ResponseModel cancelTask(@PathVariable String id, HttpServletRequest request) {
    return downloadService.cancel(id, request);
  }
}
