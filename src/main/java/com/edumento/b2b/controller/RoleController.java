package com.edumento.b2b.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.role.AssignRoleModel;
import com.edumento.b2b.model.role.RoleByModel;
import com.edumento.b2b.model.role.RoleCreateModel;
import com.edumento.b2b.services.RoleService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 3/29/16. */
@RestController
@RequestMapping("/api/role")
public class RoleController extends AbstractController<RoleCreateModel, Long> {

  @Autowired RoleService roleService;

  @Override
  @RequestMapping(method = RequestMethod.POST)
  //  @ApiOperation(value = "Create Role", notes = "this method is used to create role")
  public ResponseModel create(@RequestBody @Validated RoleCreateModel roleCreateModel) {
    return roleService.createRole(roleCreateModel);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
  //  @ApiOperation(value = "Update Role", notes = "this method is used to update role by id")
  public ResponseModel update(
      @PathVariable("id") Long id, @RequestBody @Validated RoleCreateModel roleCreateModel) {
    return roleService.updateRole(id, roleCreateModel);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  //  @ApiOperation(value = "Get Role", notes = "this method is used to get role by id")
  public ResponseModel get(@PathVariable("id") Long id) {
    return roleService.getRole(id);
  }

  @RequestMapping(path = "/{id}/users", method = RequestMethod.GET)
  //  @ApiOperation(value = "Get users", notes = "this method is used to get users on role by id")
  public ResponseModel getUserOnRole(@PathVariable("id") Long id) {
    return roleService.getUserOnRole(id);
  }

  @RequestMapping(path = "/{id}/assign", method = RequestMethod.POST)
  //  @ApiOperation(
  //    value = "Assign Role",
  //    notes = "this method is used to assign role on list of users"
  //  )
  public ResponseModel assign(@PathVariable("id") Long id, @RequestBody List<Long> usersIds) {
    return roleService.assignRole(id, usersIds);
  }

  @RequestMapping(path = "/{id}/unassign", method = RequestMethod.POST)
  //  @ApiOperation(
  //    value = "Unassign Role",
  //    notes = "this method is used to unassign role from list of users"
  //  )
  public ResponseModel unassign(@PathVariable("id") Long id, @RequestBody List<Long> usersIds) {
    return roleService.unassignRole(id, usersIds);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
  //  @ApiOperation(value = "Delete Role", notes = "this method is used to delete role by id")
  public ResponseModel delete(@PathVariable("id") Long id) {
    return roleService.delete(id);
  }

  @RequestMapping(method = RequestMethod.DELETE)
  //  @ApiOperation(value = "Delete Roles", notes = "this method is used to delete a list of roles")
  public ResponseModel delete(@RequestBody List<Long> ids) {
    return roleService.delete(ids);
  }

  @Override
  @RequestMapping(method = RequestMethod.GET)
  //  @ApiOperation(value = "Get Roles", notes = "this method is used to get current user roles")
  public ResponseModel get() {
    return roleService.getRoles();
  }

  @Deprecated
  @RequestMapping(path = "/assign", method = RequestMethod.POST)
  //  @ApiOperation(value = "Assign", notes = "this method is used to Assign Role ", hidden = true)
  public ResponseModel assign(@RequestBody @Validated AssignRoleModel assignRoleModel) {
    return roleService.assignRole(assignRoleModel);
  }

  @RequestMapping(path = "/by", method = RequestMethod.POST)
  //  @ApiOperation(
  //    value = "Get Role",
  //    notes = "this method is used to get role by organization , foundation and user type"
  //  )
  public ResponseModel getRoleBy(@RequestBody @Validated RoleByModel roleByModel) {
    return roleService.getRoleBy(roleByModel);
  }
}
