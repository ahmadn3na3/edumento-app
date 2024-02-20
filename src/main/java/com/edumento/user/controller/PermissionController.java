package com.edumento.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.core.model.ResponseModel;
import com.edumento.user.services.ModuleService;

import jakarta.servlet.http.HttpServletRequest;

/** Created by ahmad on 5/15/16. */
@RestController
public class PermissionController {
	private final Logger log = LoggerFactory.getLogger(PermissionController.class);

	private final ModuleService moduleService;

	public PermissionController(ModuleService moduleService) {
		this.moduleService = moduleService;
	}

	@RequestMapping(path = "/api/module", method = RequestMethod.GET)
	// @ApiOperation(value = "Get Modules", notes = "this method is used to list all
	// modules")
	public ResponseModel get(@RequestHeader(required = false) Integer page,
			@RequestHeader(required = false) Integer size, HttpServletRequest request) {
		return moduleService.getModules();
	}

	@RequestMapping(path = "/api/module/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "Get Module", notes = "this method is used to get
	// module by id")
	public ResponseModel get(@PathVariable Long id) {
		return moduleService.getModule(id);
	}

	@RequestMapping("/api/permission")
	// @ApiOperation(
	// value = "Get Permissions",
	// notes = "this method is used to list all user permissions"
	// )
	public ResponseModel getAll(HttpServletRequest request) {
		return moduleService.getPermissions();
	}
}
