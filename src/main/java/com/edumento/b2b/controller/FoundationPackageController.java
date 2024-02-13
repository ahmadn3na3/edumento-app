package com.edumento.b2b.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.b2b.model.foundationpackage.FoundationPackageCreateModel;
import com.edumento.b2b.services.FoundationPackageService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 4/18/17. */
@RestController
@RequestMapping("api/package/foundation")
public class FoundationPackageController
    extends AbstractController<FoundationPackageCreateModel, Long> {
  @Autowired
  FoundationPackageService foundationPackageService;

  @Override
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
  // @ApiOperation(
  // value = "Create foundation package",
  // response = ResponseModel.class,
  // notes = "this method is used to create new foundtion package"
  // )
  public ResponseModel create(
      @RequestBody FoundationPackageCreateModel foundationPackageCreateModel) {
    return foundationPackageService.createFoundationPackage(foundationPackageCreateModel);
  }

  @Override
  @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  // @ApiOperation(
  // value = "update foundation package",
  // notes = "this method is used to update foundation package using foundation
  // id"
  // )
  public ResponseModel update(
      @PathVariable Long id,
      @RequestBody FoundationPackageCreateModel foundationPackageCreateModel) {
    return foundationPackageService.updateFoundationPackage(id, foundationPackageCreateModel);
  }

  @Override
  @DeleteMapping(path = "/{id}")
  // @ApiOperation(
  // value = "delete foundation package",
  // notes = "this method is used to delete foundation package by package id"
  // )
  public ResponseModel delete(@PathVariable Long id) {
    return foundationPackageService.deleteFoundationPackage(id);
  }

  @Override
  @RequestMapping(method = RequestMethod.GET)
  // @ApiOperation(
  // value = "Get Foundation Packages",
  // response = FoundationPackageModel.class,
  // notes = "this method is used to list all foundation packages"
  // )
  public ResponseModel get(
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size) {
    return foundationPackageService.getAll(PageRequestModel.getPageRequestModel(page, size));
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
  // @ApiOperation(
  // value = "Get Foundation Package",
  // response = FoundationPackageModel.class,
  // notes = "this method is used to get foundation package by id"
  // )
  public ResponseModel get(@PathVariable Long id) {
    return foundationPackageService.get(id);
  }

  @RequestMapping(path = "/{id}/assign", method = RequestMethod.POST)
  // @ApiOperation(
  // value = "Assign package",
  // response = ResponseModel.class,
  // notes = "this method is used to add foundation in package"
  // )
  public ResponseModel assign(
      @PathVariable Long id, @RequestBody(required = true) List<Long> foundationIds) {
    return foundationPackageService.assign(id, foundationIds);
  }

  @RequestMapping(path = "/{id}/unassign", method = RequestMethod.POST)
  // @ApiOperation(
  // value = "Unassign Package ",
  // response = ResponseModel.class,
  // notes = "this method is used to remove foundation from package"
  // )
  public ResponseModel unassign(
      @PathVariable Long id, @RequestBody(required = true) List<Long> foundationIds) {
    return foundationPackageService.unassign(id, foundationIds);
  }
}
