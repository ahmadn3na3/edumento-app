package com.edumento.b2b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.timelock.TimeLockCreateModel;
import com.edumento.b2b.services.TimeLockService;
import com.edumento.core.constants.SortDirection;
import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 7/31/16. */
@RestController
@RequestMapping("/api/timelock")
public class TimeLockController extends AbstractController<TimeLockCreateModel, Long> {
	@Autowired
	TimeLockService timeLockService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
//  @ApiOperation(value = "Create Timelock", notes = "this method is used to create new time lock")
	public ResponseModel create(@RequestBody @Validated TimeLockCreateModel timeLockCreateModel) {
		return timeLockService.create(timeLockCreateModel);
	}

	@RequestMapping(method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Timelock",
//    notes = "this method is used to get time lock in organization"
//  )
	public ResponseModel get(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size, @RequestHeader(required = false) Long organizationId,
			@RequestHeader(required = false, defaultValue = "NAME") SortField field,
			@RequestHeader(required = false, defaultValue = "ASCENDING") SortDirection sortDirection) {

		return timeLockService.getAll(PageRequestModel.getPageRequestModel(page, size,
				Sort.by(sortDirection.getValue(), field.getFieldName())), organizationId);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
//  @ApiOperation(value = "Get Timelock", notes = "this method is used to get time lock by id")
	public ResponseModel get(@PathVariable Long id) {
		return timeLockService.getById(id);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//  @ApiOperation(value = "Update timelock", notes = "this method is used to update timelock by id")
	public ResponseModel update(@PathVariable Long id,
			@RequestBody @Validated TimeLockCreateModel timeLockCreateModel) {
		return timeLockService.update(id, timeLockCreateModel);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(value = "Delete timelock", notes = "this method is used to delete time lock by id")
	public ResponseModel delete(@PathVariable Long id) {
		return timeLockService.delete(id);
	}

	@RequestMapping(path = "/{id}/duplicate", method = RequestMethod.GET)
//  @ApiOperation(value = "Duplicate timelock", notes = "this method is used to duplicate time lock")
	public ResponseModel duplicate(@PathVariable Long id) {
		return timeLockService.duplicate(id);
	}

	@RequestMapping(path = "/validate", method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Validate Unlock password",
//    notes = "this method is used to validate unlock password of a timelock"
//  )
	public ResponseModel validate(@RequestBody String password) {
		return timeLockService.validateUnlockPassword(password);
	}
}
