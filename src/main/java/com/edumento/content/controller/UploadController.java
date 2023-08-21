package com.edumento.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.edumento.content.services.ResourceUploadService;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 5/29/16. */
@RestController
@RequestMapping("/upload")
public class UploadController {

  @Autowired
  ResourceUploadService uploadService;

  @RequestMapping(path = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
  // @ApiOperation(value = "Upload Image", notes = "this method is used to upload
  // image and thumbnail")
  public ResponseModel uploadImage(
      @RequestParam("image") MultipartFile img,
      @RequestParam(name = "thumbnail", required = false) MultipartFile thumb) {
    return uploadService.uploadImageAndThumbnail(img, thumb);
  }

  @RequestMapping(path = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
  // @ApiOperation(value = "Upload Audio", notes = "this method is used to upload
  // audio file")
  public ResponseModel uploadImage(@RequestParam("file") MultipartFile img) {
    return uploadService.uploadAudioFile(img);
  }
}
