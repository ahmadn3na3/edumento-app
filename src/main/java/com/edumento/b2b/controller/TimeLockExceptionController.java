package com.edumento.b2b.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.timelock.TimeLockExceptionCreationModel;
import com.edumento.b2b.services.TimeLockService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 7/31/16. */
@RestController
@RequestMapping("/api/timelock/{lockId}/exception")
public class TimeLockExceptionController extends AbstractController<TimeLockExceptionCreationModel, Long> {

	@Autowired
	TimeLockService timeLockService;

	@RequestMapping(method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Create time lock exception",
//    notes = "this method is used to create time lock exception"
//  )
	public ResponseModel create(@PathVariable Long lockId,
			@RequestBody TimeLockExceptionCreationModel timeLockExceptionCreationModel) {
		return timeLockService.createException(lockId, timeLockExceptionCreationModel);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
//  @ApiOperation(
//    value = "Update timelock exception",
//    notes = "this method is used to update timelock exception"
//  )
	public ResponseModel update(@PathVariable Long id, @PathVariable Long lockId,
			@RequestBody TimeLockExceptionCreationModel timeLockExceptionCreationModel) {
		return timeLockService.updateException(id, lockId, timeLockExceptionCreationModel);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(
//    value = "Delete Time lock exception",
//    notes = "this method is used to delete time lock exception by id"
//  )
	public ResponseModel create(@PathVariable Long id, @PathVariable Long lockId) {
		return timeLockService.deleteException(id, lockId);
	}
}
