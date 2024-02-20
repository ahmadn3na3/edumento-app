package com.edumento.content.resources;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.content.models.ContentCreateModel;
import com.edumento.content.models.ContentUserData;
import com.edumento.content.services.ContentService;
import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.SortField;
import com.edumento.core.model.DateModel;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 7/2/16. */
@RestController
@RequestMapping("/api/content")
public class ContentController {
	@Autowired
	ContentService contentService;

	@RequestMapping(method = RequestMethod.POST)
	// @ApiOperation(value = "create content", notes = "this method is used to
	// create new content")
	public ResponseModel create(@RequestBody @Validated ContentCreateModel contentCreateModel,
			HttpServletRequest request) {
		return contentService.createContent(contentCreateModel);
	}

	@RequestMapping(path = "/space/{id}/shelves", method = RequestMethod.GET)
	// @ApiOperation(value = "get shelves", notes = "this method is used to get
	// shelves by content id ")
	public ResponseModel getShelves(@PathVariable Long id, HttpServletRequest request) {
		return contentService.getShelves(id);
	}

	@RequestMapping(path = "/space/{id}", method = RequestMethod.GET)
	// @ApiOperation(
	// value = "get space content",
	// notes = "this method is used to get space content by space id"
	// )
	public ResponseModel getAll(@PathVariable Long id, @RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size,
			@RequestHeader(required = false, defaultValue = "NAME") SortField field,
			@RequestHeader(required = false, defaultValue = "ASC") Sort.Direction direction,
			@RequestParam(required = false, name = "shelf") String shelf) {
		var sort = Sort.by(direction, field.getFieldName());
		return contentService.getSpaceContents(id, PageRequestModel.getPageRequestModel(page, size, sort), shelf);
	}

	@RequestMapping(path = "/space/{id}", method = RequestMethod.POST)
	// @ApiOperation(value = "get space content update", notes = "this method is
	// used to get space content update by content id")
	public ResponseModel getAll(@PathVariable Long id, @RequestBody DateModel dateModel, HttpServletRequest request) {
		return contentService.getSpaceContentsUpdates(id, dateModel);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	// @ApiOperation(value = "Update content", notes = "this method is used to
	// update content by contact id")
	public ResponseModel update(@PathVariable Long id, @RequestBody @Validated ContentCreateModel contentCreateModel,
			HttpServletRequest request) {
		return contentService.update(id, contentCreateModel);
	}

	@RequestMapping(path = "/{id}/user", method = RequestMethod.PUT)
	// @ApiOperation(value = "update content's user data ", notes = "this method is
	// used to update content's user data")
	public ResponseModel update(@PathVariable Long id, @RequestBody @Validated ContentUserData contentUserData,
			HttpServletRequest request) {
		return contentService.updateContentUserData(id, contentUserData);
	}

	@RequestMapping(path = "/{id}/status/{status}", method = RequestMethod.PUT)
	// @ApiOperation(value = "Update Content Status", notes = "this method is used
	// to update content status by content id")
	public ResponseModel update(@PathVariable Long id, @PathVariable ContentStatus status, HttpServletRequest request) {
		return contentService.updateStatus(id, status);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	// @ApiOperation(value = "Delete Content", notes = "this method is used to
	// delete content by id")
	public ResponseModel delete(@PathVariable Long id, HttpServletRequest request) {
		return contentService.delete(id);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "Get Content", notes = "this method is used to get
	// content by id")
	public ResponseModel get(@PathVariable Long id, HttpServletRequest request) {
		return contentService.getById(id);
	}

	@RequestMapping(path = "parent_space/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "Get parent space of Content", notes = "this method is
	// used to get space by content id")
	public ResponseModel getParentSpaceById(@PathVariable Long id, HttpServletRequest request) {
		return contentService.getParentSpaceById(id);
	}

	@RequestMapping(path = "/contentType", method = RequestMethod.GET)
	// @ApiOperation(value = "Get Content Types", notes = "this method is used to
	// get content by id")
	public ResponseModel getContentType() {

		return ResponseModel.done(Arrays.stream(ContentType.values())
				.collect(Collectors.toMap(ContentType::name, ContentType::getExtentions)));
	}

	@RequestMapping(path = "copy/{id}/to/{spaceId}", method = RequestMethod.GET)
	// @ApiOperation(value = "Copy Content to space", notes = "this method is used
	// to copy content to space , using content id and space id")
	public ResponseModel get(@PathVariable Long id, @PathVariable Long spaceId, HttpServletRequest request) {
		return contentService.copyContentToSpace(id, spaceId);
	}
}
