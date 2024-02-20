package com.edumento.b2b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.foundation.FoundationCreateModel;
import com.edumento.b2b.services.FoundationService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.ResponseModel;

/** Created by ayman on 02/06/16. */
@RestController()
@RequestMapping("/api/foundation")
public class FoundationController extends AbstractController<FoundationCreateModel, Long> {
	@Autowired
	FoundationService foundationService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
//  @ApiOperation(value = "Create Foundation", notes = "this method is used to create new Foundation")
	public ResponseModel create(@RequestBody @Validated FoundationCreateModel createModel) {
		return foundationService.create(createModel);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//  @ApiOperation(
//    value = "Update foundation",
//    notes = "this method is used to update foundation by id"
//  )
	public ResponseModel update(@PathVariable Long id, @RequestBody @Validated FoundationCreateModel updateModel) {

		return foundationService.update(id, updateModel);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
//  @ApiOperation(value = "Get foundation", notes = "this method is used to get foundation by id")
	public ResponseModel get(@PathVariable Long id) {
		return foundationService.getFoundation(id);
	}

	@Override
	@RequestMapping(method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get all Foundations",
//    response = FoundationModel.class,
//    notes = "this method is used to list all foundations"
//  )
	public ResponseModel get() {
		return foundationService.getAllFoundation();
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(
//    value = "Delete foundation",
//    notes = "this method is used to delete foundation by id"
//  )
	public ResponseModel delete(@PathVariable Long id) {
		return foundationService.delete(id);
	}

	@RequestMapping(path = "/{id}/toggleStatus", method = RequestMethod.PUT)
//  @ApiOperation(
//    value = "Change Foundation Status",
//    notes = "this method is used to change status of foundation by id"
//  )
	public ResponseModel changeStatus(@PathVariable Long id) {
		return foundationService.changeFoundationStatus(id);
	}

	@RequestMapping(path = "/{id}/spaces", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Foundation Spaces",
//    notes = "this method is used to list all spaces in foundation by foundation id"
//  )
	public ResponseModel getSpaces(@PathVariable Long id) {
		return foundationService.getSpacesByFoundationId(id);
	}

	@RequestMapping(path = "/{id}/categories", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get categories on foundation ",
//    notes = "this method is used to list categories in foundation by foundation id"
//  )
	public ResponseModel getCategories(@PathVariable Long id) {
		return foundationService.getCategoriesByFoundationId(id);
	}

	@RequestMapping(path = "/{id}/organizations", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Organization",
//    notes = "this method is used to list organization by foundation id "
//  )
	public ResponseModel getOrganizationsByFoundation(@PathVariable Long id) {
		return foundationService.getAllOrganizationByFoundation(id);
	}

	@RequestMapping(path = "/{id}/users", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "get users",
//    notes = "this method is used to get users in foundation by foundation id"
//  )
	public ResponseModel getUsers(@PathVariable Long id) {
		return foundationService.getUsersInFoundation(id);
	}
}
