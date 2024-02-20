package com.edumento.b2b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.organization.OrganizationCreateModel;
import com.edumento.b2b.services.OrganizationService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 3/2/16. */
@RestController()
@RequestMapping("/api/organization")
public class OrganizationController extends AbstractController<OrganizationCreateModel, Long> {

	@Autowired
	OrganizationService organizationService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Create Organization",
//    notes = "this method is used to create new organization"
//  )
	public ResponseModel create(@RequestBody @Validated OrganizationCreateModel createModel) {
		return organizationService.create(createModel);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//  @ApiOperation(
//    value = "Update Organization",
//    notes = "this method is used to update organization by organization id"
//  )
	public ResponseModel update(@PathVariable Long id, @RequestBody @Validated OrganizationCreateModel updateModel) {
		return organizationService.update(id, updateModel);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Organization",
//    response = OrganizationModel.class,
//    notes = "this method is used to get organization by id"
//  )
	public ResponseModel get(@PathVariable Long id) {
		return organizationService.getOrganization(id);
	}

	@RequestMapping(path = "/{id}/toggleStatus", method = RequestMethod.PUT)
//  @ApiOperation(
//    value = "Change organization's status",
//    notes = "this method is used to change status of an organization by organization id"
//  )
	public ResponseModel changeStatus(@PathVariable Long id, HttpServletRequest request) {
		return organizationService.changeOrganizationStatus(id);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get All Organizations",
//    notes = "this method is used to list all organizations"
//  )
	public ResponseModel get(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size) {
		return organizationService.getAllOrganization(PageRequestModel.getPageRequestModel(page, size));
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(
//    value = "Delete Organization",
//    notes = "this method is used to delete an organization by id"
//  )
	public ResponseModel delete(@PathVariable Long id) {
		return organizationService.delete(id);
	}

	@Deprecated
	@RequestMapping(path = "/{id}/users", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get users",
//    notes = "this method is used to list users in organization",
//    hidden = true
//  )
	public ResponseModel getUsers(@PathVariable Long id) {
		return organizationService.getUsersInOrganization(id);
	}

	@Deprecated
	@RequestMapping(path = "/{id}/groups", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Groups",
//    notes = "this method is used to list groups in organization",
//    hidden = true
//  )
	public ResponseModel getGroups(@PathVariable Long id) {
		return organizationService.getGroupsInOrganization(id);
	}

	@RequestMapping(path = "/{id}/spaces", method = RequestMethod.GET)
//  @ApiOperation(value = "Get Spaces", notes = "this method is used to list spaces in organization")
	public ResponseModel getSpaces(@PathVariable Long id) {
		return organizationService.getSpacesByOrganizationId(id);
	}

	@RequestMapping(path = "/{id}/categories", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Categories",
//    notes = "this method is used to list all categories in organization"
//  )
	public ResponseModel getCategories(@PathVariable Long id) {
		return organizationService.getCategoriesByOrganizationId(id);
	}

	@Deprecated
	@RequestMapping(path = "/foundation/{id}", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Organization in Foundation",
//    notes =
//        "this method is used to list all organizations in specific organization , by foundation id"
//  )
	public ResponseModel getOrganizationsByFoundation(@PathVariable Long id) {
		return organizationService.getAllOrganizationByFoundation(id);
	}
}
