package com.edumento.space.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.SimpleModel;
import com.edumento.space.model.space.request.SpaceShareModel;
import com.edumento.space.services.CommunityService;
import com.edumento.space.services.SpaceService;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 5/23/16. */
@RestController
@RequestMapping("/api/community")
public class CommunityController {
  @Autowired SpaceService spaceService;
  @Autowired CommunityService communityService;

  @RequestMapping(path = "/toggle_share", method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Toggel share",
//    notes = "this method is used to share and unshare space with users"
//  )
  public ResponseModel toggleShare(@RequestBody @Validated SpaceShareModel shareModel) {
    if (shareModel.isUnShare()) {
      return spaceService.unShareSpaceToUsers(shareModel.getSpaceId(), shareModel);
    }
    return spaceService.shareSpaceToUsers(shareModel.getSpaceId(), shareModel);
  }

  @RequestMapping(path = "/toggle_block/{id}", method = RequestMethod.POST)
//  @ApiOperation(value = "Toggle Block", notes = "this method is used to block and unblock user")
  public ResponseModel toggleBlock(@PathVariable Long id, HttpServletRequest request) {
    return communityService.toggleBlock(id);
  }

  @RequestMapping(path = "/toggle_follow/{id}", method = RequestMethod.POST)
//  @ApiOperation(value = "Toggle follow", notes = "this method is used to follow and unfollow user")
  public ResponseModel toggleFollow(@PathVariable Long id) {
    return communityService.toggleFollow(id);
  }

  // TODO:Paging to be discussed
  @RequestMapping(path = "/space/{id}", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "get space community",
//    response = SpaceCommunityModel.class,
//    notes = "this method is used to list space community"
//  )
  public ResponseModel getSpaceCommunity(
      @PathVariable Long id, @RequestHeader(required = false) boolean addCurrent) {
    return communityService.getSpaceCommunity(id, addCurrent);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.POST)
//  @ApiOperation(value = "Search Community", notes = "this method is used to list users to share")
  public ResponseModel searchCommunity(
      @PathVariable Long id,
      @RequestBody SimpleModel search,
      @RequestHeader(required = false, defaultValue = "0") Integer page,
      @RequestHeader(required = false, defaultValue = "20") Integer size) {
    return communityService.listUserToShare(id, search.getName(), page, size);
  }
}
