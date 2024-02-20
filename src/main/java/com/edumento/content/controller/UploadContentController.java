package com.edumento.content.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.edumento.content.models.InitUploadModel;
import com.edumento.content.services.ContentUploadService;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 6/22/16. */
@RestController
@RequestMapping("/api/content/upload")
public class UploadContentController {

	@Autowired
	ContentUploadService uploadService;

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	// @ApiOperation(
	// value = "Start Upload",
	// notes = "this method is used to start upload task by content id"
	// )
	public ResponseModel startUpload(@PathVariable Long id) {
		return uploadService.startUpload(id);
	}

	@RequestMapping(path = "/init", method = RequestMethod.POST)
	// @ApiOperation(
	// value = "Initiate Upload",
	// notes = "this method is used to start upload task by InitUploadModel "
	// )
	public ResponseModel startUpload(@RequestBody InitUploadModel initUploadModel) {
		return uploadService.startUpload(initUploadModel);
	}

	@RequestMapping(method = RequestMethod.POST)
	// @ApiOperation(value = "Resume Upload", notes = "this method is used to resume
	// upload task")
	public ResponseModel resume(@RequestBody MultipartFile file, @RequestHeader(name = "uid") String uploadId,
			@RequestHeader(name = "cix", defaultValue = "0") Integer chunkIndex) {
		return uploadService.resume(file, uploadId, chunkIndex);
	}

	@RequestMapping(path = "/commit/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "Commit", notes = "this method is used to Commit upload
	// task")
	public ResponseModel commit(@PathVariable String id, HttpServletRequest request) {
		return uploadService.commit(id, request);
	}

	@RequestMapping(path = "/cancel/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "cancel Upload", notes = "this method is used to cancel
	// upload task")
	public ResponseModel cancel(@PathVariable String id, HttpServletRequest request) {
		return uploadService.cancel(id, request);
	}
}
