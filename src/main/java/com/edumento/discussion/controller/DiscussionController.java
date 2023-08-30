package com.edumento.discussion.controller;

import com.edumento.core.constants.DiscussionType;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.DateModel;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.util.DateConverter;
import com.edumento.discussion.model.discussion.CommentCreateModel;
import com.edumento.discussion.model.discussion.DiscussionCreateModel;
import com.edumento.discussion.services.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/** Created by ayman on 25/08/16. */
@RestController
@RequestMapping("/api/discussion")
public class DiscussionController extends AbstractController<DiscussionCreateModel, String> {
	@Autowired
	DiscussionService discussionService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
//	@ApiOperation(value = "Create Discussion", notes = "this method is used to create new discussion")
	public ResponseModel create(@RequestBody @Validated DiscussionCreateModel discussionCreateModel) {
		return discussionService.createNewDiscussion(discussionCreateModel);
	}

	@Override
	@RequestMapping(path = "/update/{id}", method = RequestMethod.PUT)
//	@ApiOperation(value = "update discussion", notes = "this method is used to update discussion", hidden = false)
	public ResponseModel update(@PathVariable("id") String id,
			@RequestBody @Validated DiscussionCreateModel discussionCreateModel) {
		return discussionService.update(id, discussionCreateModel);
	}

	@RequestMapping(path = "/listAll/{spaceId}", method = RequestMethod.GET)
//	@ApiOperation(value = "List Discussions", notes = "this method is used to list all discussion on a space")
	public ResponseModel listAllDiscussion(@PathVariable("spaceId") Long spaceId,
			@RequestHeader(required = false) Integer page, @RequestHeader(required = false) Integer size) {

		return discussionService.getAllDiscussions(spaceId, DiscussionType.DISCUSSION,
				PageRequestModel.getPageRequestModel(page, size,  Sort.by(Sort.Direction.DESC, "creationDate")));
	}

	@GetMapping("/listAllInquiry/{spaceId}")
//	@ApiOperation(value = "List Inquiries", notes = "this method is used to list all discussion on a space")
	public ResponseModel listAllInquiry(@PathVariable("spaceId") Long spaceId,
			@RequestHeader(required = false) Integer page, @RequestHeader(required = false) Integer size) {

		return discussionService.getAllDiscussions(spaceId, DiscussionType.INQUIRY,
				PageRequestModel.getPageRequestModel(page, size,  Sort.by(Sort.Direction.DESC, "modificationDate")));
	}

	@RequestMapping(path = "/get/{id}", method = RequestMethod.GET)
//	@ApiOperation(value = "get discussion", notes = "this method is used to get discussion by id")
	public ResponseModel getDiscussion(@PathVariable("id") String id) {
		return discussionService.get(id);
	}

	@RequestMapping(path = "/reply/{id}", method = RequestMethod.POST)
//	@ApiOperation(value = "Comment on discussion", notes = "this method is used to add comment on discussion")
	public ResponseModel reply(@PathVariable("id") String id,
			@RequestBody @Validated CommentCreateModel commentCreateModel) {
		return discussionService.addReply(id, commentCreateModel);
	}

	@RequestMapping(path = "/reply/{id}", method = RequestMethod.DELETE)
//	@ApiOperation(value = "Delete Comment", notes = "this method is used to delete comment from discussion")
	public ResponseModel deleteReply(@PathVariable("id") String id) {
		return discussionService.deleteReply(id);
	}

	@RequestMapping(path = "/reply/{id}", method = RequestMethod.PUT)
//	@ApiOperation(value = "Edit Comment", notes = "this method is used to edit comment on discussion")
	public ResponseModel editReply(@PathVariable("id") String id, @RequestBody CommentCreateModel body) {
		return discussionService.editReply(id, body.getCommentBody());
	}

	@RequestMapping(path = "/reply/{id}/like", method = RequestMethod.GET)
//	@ApiOperation(value = "Like Comment", notes = "this method is used to like comment by id")
	public ResponseModel like(@PathVariable("id") String id) {
		return discussionService.like(id);
	}

	@Deprecated
	@RequestMapping(path = "/updates/{spaceId}", method = RequestMethod.POST)
//	@ApiOperation(value = "", notes = "this method is used to", hidden = true)
	public ResponseModel getUpdates(@PathVariable("spaceId") Long spaceId, @RequestBody @Validated DateModel since) {
		return discussionService.getUpdates(spaceId, DateConverter.convertZonedDateTimeToDate(since.getDate()));
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//	@ApiOperation(value = "Delete", notes = "this method is used to delete discussion by id")
	public ResponseModel delete(@PathVariable("id") String id) {
		return discussionService.delete(id);
	}
}
