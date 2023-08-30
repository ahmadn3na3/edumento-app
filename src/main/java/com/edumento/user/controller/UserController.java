package com.edumento.user.controller;

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

import com.edumento.core.constants.SortDirection;
import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;
import com.edumento.user.constant.UserType;
import com.edumento.user.model.user.UserOrganizationCreateModel;
import com.edumento.user.model.user.UserSearchModel;
import com.edumento.user.services.AccountService;
import com.edumento.user.services.UserAdministrationService;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 2/17/16. */
@RestController
@RequestMapping("/api/user")
public class UserController extends AbstractController<Object, Long> {
	@Autowired
	AccountService accountService;

	@Autowired
	UserAdministrationService userAdministrationService;

	@RequestMapping(method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Users",
//    notes =
//        "this method is used to get users by foundation , organization , type and (full name or username)"
//  )
	public ResponseModel getUsers(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size, @RequestHeader(required = false) Long foundationId,
			@RequestHeader(required = false) Long organizationId, @RequestHeader(required = false) UserType type,
			@RequestParam(required = false) String filter,
			@RequestHeader(required = false, defaultValue = "FULL_NAME") SortField field,
			@RequestHeader(required = false, defaultValue = "ASCENDING") SortDirection sortDirection,
			@RequestHeader(required = false, defaultValue = "false") boolean all) {
		return userAdministrationService.getUsers(
				PageRequestModel.getPageRequestModel(page, size,
						Sort.by(sortDirection.getValue(), field.getFieldName())),
				foundationId, organizationId, type, filter, all);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/cloud")
	@Deprecated
//  @ApiOperation(
//    value = "Get Cloud Users",
//    notes = "this method is used to get cloud users",
//    hidden = true
//  )
	public ResponseModel getCloudUsers(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size, HttpServletRequest request) {
		return userAdministrationService.getCloudUsers(PageRequestModel.getPageRequestModel(page, size));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/systemAdmins")
//  @ApiOperation(value = "", notes = "this method is used to", hidden = true)
	@Deprecated
	public ResponseModel getSystemAdmins(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size, HttpServletRequest request) {
		return userAdministrationService.getSystemAdmins(PageRequestModel.getPageRequestModel(page, size));
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}/reset_password")
//  @ApiOperation(value = "Reset Password", notes = "this method is used to  reset user password")
	public ResponseModel resetPassword(@PathVariable Long id, HttpServletRequest request) {
		return userAdministrationService.restPassword(id);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/{id}")
//  @ApiOperation(value = "Get User Info", notes = "this method is used to get user infromation")
	public ResponseModel getUserInfo(@PathVariable Long id, HttpServletRequest request) {
		return accountService.getUserWithAuthorities(id);
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/{id}/toggleStatus")
//  @ApiOperation(value = "Activate User", notes = "this method is used to activate user")
	public ResponseModel activateUser(@PathVariable Long id, HttpServletRequest request) {
		return userAdministrationService.changeUserStatus(id);
	}

	@RequestMapping(method = RequestMethod.POST)
//  @ApiOperation(value = "Create User", notes = "this method is used to create user")
	public ResponseModel createUser(@RequestBody @Validated UserOrganizationCreateModel createModel,
			HttpServletRequest request) {

		String baseUrl = request.getScheme() + // "http"
				"://" + // "://"
				request.getServerName() + // "myhost"
				":" + // ":"
				request.getServerPort() + // "80"
				request.getContextPath();
		return userAdministrationService.createUser(createModel, baseUrl);
		// "/myContextPath" or "" if deployed in root context

	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//  @ApiOperation(value = "Update User", notes = "this method is used to update user")
	public ResponseModel updateUser(@RequestBody UserOrganizationCreateModel updateModel, @PathVariable Long id,
			HttpServletRequest request) {
		return userAdministrationService.updateUserInformation(updateModel, id);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(value = "Delete User", notes = "this method is used to delete user information")
	public ResponseModel delete(@PathVariable Long id, HttpServletRequest request) {
		return userAdministrationService.deleteUserInformation(id);
	}

	@RequestMapping(path = "/search", method = RequestMethod.POST)
//  @ApiOperation(value = "Search", notes = "this method is used to search users")
	public ResponseModel search(@RequestBody UserSearchModel userSearchModel, HttpServletRequest request) {
		return accountService.searchUser(userSearchModel);
	}
}
