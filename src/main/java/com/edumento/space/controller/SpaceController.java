package com.edumento.space.controller;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.DateModel;
import com.edumento.core.model.IdModel;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.PageResponseModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.SimpleModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.space.model.space.request.SpaceCreateModel;
import com.edumento.space.model.space.request.SpaceRateModel;
import com.edumento.space.model.space.request.SpaceRoleUpdateModel;
import com.edumento.space.services.SpaceService;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 3/2/16. */
@RestController
@RequestMapping("/api/space")
public class SpaceController extends AbstractController<SpaceCreateModel, Long> {

  @Autowired SpaceService spaceService;

  @Override
  @RequestMapping(method = RequestMethod.POST)
  //	@ApiOperation(value = "Create new space", notes = "this method is used to create a new space
  // for logged in user")
  public ResponseModel create(@RequestBody @Validated SpaceCreateModel createModel) {
    return spaceService.createSpaceForUser(createModel, SecurityUtils.getCurrentUserLogin());
  }

  @RequestMapping(path = "/checkName", method = RequestMethod.POST)
  //	@ApiOperation(value = "Check Space Name", notes = "this method is used to check if space name
  // is not duplicated")
  public ResponseModel checkSpaceNameForUser(@RequestBody String name) {
    return spaceService.checkSpaceNameForUser(name);
  }

  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  //	@ApiOperation(value = "Delete space", notes = "this method is used to delete space , it
  // requires a space id to delete ")
  public ResponseModel deleteSpace(@PathVariable("id") Long id) {
    return spaceService.deleteSpace(id);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  //	@ApiOperation(value = "Update space", notes = "this method is used to update space data")
  public ResponseModel update(
      @PathVariable("id") Long id, @RequestBody SpaceCreateModel spaceCreateModel) {
    return spaceService.updateSpace(id, spaceCreateModel);
  }

  @RequestMapping(path = "/favorite", method = RequestMethod.POST)
  //	@ApiOperation(value = "Set Space as favorite", notes = "this method is used to set space as
  // favorit")
  public ResponseModel favorite(@RequestBody @Validated IdModel id) {
    return spaceService.favoriteSpace(id.getId());
  }

  @RequestMapping(path = "/unfavorite", method = RequestMethod.POST)
  //	@ApiOperation(value = "Set Space as not favorite", notes = "this method is used to remove space
  // from favorit spaces")
  public ResponseModel unfavorite(@RequestBody @Validated IdModel spaceId) {
    return spaceService.unFavoriteSpace(spaceId.getId());
  }

  @RequestMapping(path = "/rate", method = RequestMethod.POST)
  //	@ApiOperation(value = "Rate Space", notes = "this method is used to rate space from 1 to 5")
  public ResponseModel rate(@RequestBody @Validated SpaceRateModel spaceRateModel) {
    return spaceService.rateSpace(spaceRateModel.getSpaceId(), spaceRateModel.getRating());
  }

  @RequestMapping(path = "/search", method = RequestMethod.POST)
  //	@ApiOperation(value = "Search for Space", notes = "this method is used to search in spaces for
  // a space ")
  public PageResponseModel searchForSpace(
      @RequestBody SimpleModel name,
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) SortField field,
      @RequestHeader(required = false) Sort.Direction direction) {
    Sort sort = null;
    if (field != null && direction != null) {
      sort = Sort.by(direction, field.getFieldName());
    }
    PageRequest pageRequestModel = PageRequestModel.getPageRequestModel(page, size, sort);
    return spaceService.searchForSpace(name.getName(), pageRequestModel, lang);
  }

  @RequestMapping(path = "/favoriteSpaces", method = RequestMethod.GET)
  //	@ApiOperation(value = "get favorite spaces for user", response = SpaceListingModel.class)
  public ResponseModel getFavoriteSpaces(
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) SortField field,
      @RequestHeader(required = false) Sort.Direction direction) {
    return spaceService.getFavoriteSpaces(page, size, field, direction, lang);
  }

  @RequestMapping(path = "/ownedSpaces", method = RequestMethod.GET)
  //	@ApiOperation(value = "get owned spaces", notes = "Get spaces owned by logged in user",
  // response = SpaceListingModel.class)
  public ResponseModel getOwnedSpace(
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) SortField field,
      @RequestHeader(required = false) Sort.Direction direction) {
    return spaceService.getOwnedSpaces(page, size, field, direction, lang);
  }

  @RequestMapping(path = "/recentAccessed", method = RequestMethod.GET)
  //	@ApiOperation(value = "get recent access spaces", notes = "this method is used to get the
  // recent spaceses accessed by user", response = SpaceListingModel.class)
  public ResponseModel getRecentAccessedSpaces(
      @RequestHeader(required = false, defaultValue = "en") String lang) {
    return spaceService.getRecentAccessedSpaces(lang);
  }

  @RequestMapping(method = RequestMethod.GET)
  //	@ApiOperation(value = "get all user's spaces", notes = "this method is used to list all spaces
  // that user joined and owned", response = SpaceListingModel.class)
  public ResponseModel get(
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) SortField field,
      @RequestHeader(required = false) Sort.Direction direction) {

    return spaceService.getAllSpaces(lang, null, page, size, field, direction);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/byName/{name}")
  //	@ApiOperation(value = "get all user's spaces", notes = "this method is used to list user spaces
  // filtered by name ", response = SpaceListingModel.class)
  public ResponseModel get(
      @PathVariable String name,
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) SortField field,
      @RequestHeader(required = false) Sort.Direction direction) {

    return spaceService.getAllSpaces(lang, name, page, size, field, direction);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/cloud")
  //	@ApiOperation(value = "get all cloud user's spaces ", notes = "this method is used to list
  // spaces for cloud user ", response = SpaceListingModel.class)
  public ResponseModel get(
      @RequestHeader(required = false, defaultValue = "en") String lang,
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size) {

    return spaceService.getCloudSpace(PageRequestModel.getPageRequestModel(page, size));
  }

  // new functions to handle
  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  public ResponseModel get(
      @PathVariable Long id, @RequestHeader(required = false, defaultValue = "en") String lang) {
    return spaceService.getSpaceById(id, lang);
  }

  @RequestMapping(path = "/{id}/users", method = RequestMethod.GET)
  //	@ApiOperation(value = "get spaces users ", notes = "this method is used to list all users in  a
  // given space ")
  public ResponseModel getSpaceUsers(@PathVariable Long id) {

    return spaceService.getUserBySpaceID(id);
  }

  @RequestMapping(path = "/{id}/groups", method = RequestMethod.GET)
  //	@ApiOperation(value = "get spaces groups ", notes = "this method is used to list all groups in
  // a given space ")
  public ResponseModel getSpaceGroups(@PathVariable Long id) {
    return spaceService.getGroupsBySpaceId(id);
  }

  @Deprecated
  @RequestMapping(path = "/updates", method = RequestMethod.POST)
  public ResponseModel getSpaceUpdates(@RequestBody @Validated DateModel dateModel) {
    return spaceService.getSpaceUpdates(
        ZonedDateTime.ofInstant(dateModel.getDate().toInstant(), ZoneOffset.UTC));
  }

  @RequestMapping(path = "/updateShareRole", method = RequestMethod.PUT)
  //	@ApiOperation(value = "update space role ", notes = "this method is used to change user role in
  // space")
  public ResponseModel updateShareRole(
      @RequestBody @Validated SpaceRoleUpdateModel spaceRoleUpdateModel,
      HttpServletRequest request) {
    return spaceService.changeShareRole(
        spaceRoleUpdateModel.getSpaceId(),
        spaceRoleUpdateModel.getUserId(),
        spaceRoleUpdateModel.getSpaceRole());
  }

  @RequestMapping(path = "/{id}/duplicate", method = RequestMethod.GET)
  //	@ApiOperation(value = "Duplicate space ", notes = "this method is used to duplicate space")
  public ResponseModel duplicateSpace(@PathVariable Long id) {
    return spaceService.duplicateSpace(id);
  }

  @RequestMapping(path = "/{id}/join", method = RequestMethod.GET)
  //	@ApiOperation(value = "join space", notes = "this method is used to let user join a space")
  public ResponseModel joinSpace(@PathVariable Long id) {
    return spaceService.joinSpace(id);
  }

  @RequestMapping(path = "/{id}/leave", method = RequestMethod.GET)
  //	@ApiOperation(value = "Leave space", notes = "this method is used to let user leave space")
  public ResponseModel leaveSpace(@PathVariable Long id) {
    return spaceService.leaveSpace(id);
  }

  @GetMapping(path = "/{id}/access")
  //	@ApiOperation(value = "update last access", notes = "this method is used to update user's last
  // access to a space")
  public ResponseModel access(@PathVariable Long id) {
    spaceService.updateUserLastAccess(id);
    return ResponseModel.done();
  }

  @PostMapping(path = "/autoJoin")
  //	@ApiOperation(value = "autoJoin Users", notes = "this method is used to auto join spaces with
  // tags ")
  public ResponseModel autoJoin(
      @RequestBody List<String> tags, @RequestHeader(required = false) boolean closeAutoLogin) {
    return spaceService.joinWithTags(tags, closeAutoLogin);
  }
}
