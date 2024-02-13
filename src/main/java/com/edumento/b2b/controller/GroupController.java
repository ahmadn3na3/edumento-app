package com.edumento.b2b.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.group.GroupCreateModel;
import com.edumento.b2b.services.GroupService;
import com.edumento.core.constants.SortDirection;
import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.model.ToggleStatusModel;

/** Created by ahmad on 3/7/16. */
@RestController
@RequestMapping("/api/group")
public class GroupController extends AbstractController<GroupCreateModel, Long> {

  @Autowired GroupService groupService;

  @Override
  @RequestMapping(method = RequestMethod.POST)
  public ResponseModel create(@RequestBody @Validated GroupCreateModel group) {
    return groupService.create(group);
  }

  @RequestMapping(path = "/{orgId}", method = RequestMethod.POST)
  @Deprecated
  //  @ApiOperation(value = "create", hidden = true)
  public ResponseModel create(
      @RequestBody @Validated GroupCreateModel group, @PathVariable Long orgId) {
    return groupService.create(group, orgId);
  }

  @RequestMapping(method = RequestMethod.GET)
  //  @ApiOperation(
  //    value = "Get Groups",
  //    notes = "this method is used to get all groups , can be used with filters and sorting"
  //  )
  public ResponseModel getAll(
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size,
      @RequestHeader(required = false) Long foundationId,
      @RequestHeader(required = false) Long organizationId,
      @RequestHeader(required = false) String filter,
      @RequestHeader(required = false, defaultValue = "NAME") SortField field,
      @RequestHeader(required = false, defaultValue = "ASCENDING") SortDirection sortDirection,
      @RequestHeader(required = false, defaultValue = "false") boolean all) {
    return groupService.getGroups(
        PageRequestModel.getPageRequestModel(
            page, size, Sort.by(sortDirection.getValue(), field.getFieldName())),
        foundationId,
        organizationId,
        filter,
        all);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  //  @ApiOperation(value = "Get Group", notes = "this method is used to get group by id")
  public ResponseModel get(@PathVariable Long id) {
    return groupService.getGroup(id);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  //  @ApiOperation(value = "Delete Group", notes = "this method is used to delete group by id")
  public ResponseModel delete(@PathVariable Long id) {
    return groupService.delete(id);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  //  @ApiOperation(
  //      value = "update group",
  //      notes = "this method is used to update group by id and group create model")
  public ResponseModel update(
      @PathVariable Long id, @RequestBody @Validated GroupCreateModel groupCreateModel) {
    return groupService.update(id, groupCreateModel);
  }

  @RequestMapping(path = "/toggleStatus", method = RequestMethod.POST)
  //  @ApiOperation(
  //      value = "Toggle Group Status",
  //      notes = "this method is used to activate or deactivate group's users")
  public ResponseModel toggleStatus(@RequestBody @Validated ToggleStatusModel toggleStatusModel) {
    return groupService.toggleGroupStatus(toggleStatusModel);
  }

  @RequestMapping(path = "/assign/{id}", method = RequestMethod.POST)
  //  @ApiOperation(value = "Assign user", notes = "this method is used to assign user to group")
  public ResponseModel assign(@RequestBody List<Long> usersId, @PathVariable Long id) {
    return groupService.assignUserToGroup(usersId, id);
  }

  @RequestMapping(path = "/unAssign/{id}", method = RequestMethod.POST)
  //  @ApiOperation(value = "Unassign user", notes = "this method is used to unassign user from
  // group")
  public ResponseModel unAssign(@RequestBody List<Long> usersId, @PathVariable Long id) {
    return groupService.removeUserFromGroup(usersId, id);
  }

  @RequestMapping(path = "/transfer/{fromId}/{toId}", method = RequestMethod.POST)
  //  @ApiOperation(
  //      value = "Transfer user",
  //      notes = "this method is used to transfer user from group to another")
  public ResponseModel transfer(
      @RequestBody List<Long> usersId, @PathVariable Long fromId, @PathVariable Long toId) {
    return groupService.transferUserToGroup(usersId, fromId, toId);
  }

  @RequestMapping(path = "/{id}/users", method = RequestMethod.GET)
  //  @ApiOperation(
  //      value = "Get users",
  //      notes = "this method is used to list users in group by group id")
  public ResponseModel getUsers(@PathVariable Long id) {
    return groupService.getUsers(id);
  }

  @RequestMapping(path = "/{id}/spaces", method = RequestMethod.GET)
  //  @ApiOperation(
  //      value = "get spaces",
  //      notes = "this method is used to list spaces in group by group id")
  public ResponseModel getSPaces(@PathVariable Long id) {
    return groupService.getSpacesByGroupId(id);
  }
}
