package com.edumento.b2c.controller;

import com.edumento.b2c.model.CloudPackageCreateModel;
import com.edumento.b2c.service.CloudPackageService;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** Created by ahmad on 4/18/17. */
@RestController
@RequestMapping("api/package/cloud")
public class CloudPackageController extends AbstractController<CloudPackageCreateModel, Long> {
  @Autowired
  CloudPackageService cloudPackageService;

  @Override
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
//  @ApiOperation(
//    value = "Create Cloud Package",
//    response = ResponseModel.class,
//    notes = "this method is used to create new cloud package"
//  )
  public ResponseModel create(@RequestBody CloudPackageCreateModel cloudPackageCreateModel) {
    return cloudPackageService.create(cloudPackageCreateModel);
  }

  @Override
  @RequestMapping(
    path = "/{id}",
    method = RequestMethod.PUT,
    consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
  )
//  @ApiOperation(
//    value = "Update cloud package",
//    response = ResponseModel.class,
//    notes = "this method is used to update cloud package"
//  )
  public ResponseModel update(
      @PathVariable Long id, @RequestBody CloudPackageCreateModel cloudPackageCreateModel) {
    return cloudPackageService.update(id, cloudPackageCreateModel);
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
//  @ApiOperation(
//    value = "Delete cloud package",
//    response = ResponseModel.class,
//    notes = "this method is used to delete cloud package"
//  )
  public ResponseModel delete(@PathVariable Long id) {
    return cloudPackageService.delete(id);
  }

  @Override
  @RequestMapping(method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get cloud packages",
//    response = ResponseModel.class,
//    notes = "this method is used to list cloud packages"
//  )
  public ResponseModel get(
      @RequestHeader(required = false) Integer page,
      @RequestHeader(required = false) Integer size) {
    return cloudPackageService.get(PageRequestModel.getPageRequestModel(page, size));
  }

  @Override
  @RequestMapping(path = "/{id}", method = RequestMethod.GET)
//  @ApiOperation(
//    value = "Get Cloud Packge Info",
//    response = ResponseModel.class,
//    notes = "this method is used to get a cloud package information"
//  )
  public ResponseModel get(@PathVariable Long id) {
    return cloudPackageService.get(id);
  }

  @RequestMapping(path = "/{id}/assign", method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Assign users to a cloud package",
//    response = ResponseModel.class,
//    notes = "this method is used to add users to specific cloud package"
//  )
  public ResponseModel assign(
      @PathVariable Long id, @RequestBody(required = true) List<Long> users) {
    return cloudPackageService.assign(id, users);
  }

  @RequestMapping(path = "/{id}/unassign", method = RequestMethod.POST)
//  @ApiOperation(
//    value = "Unassign users from a cloud package",
//    response = ResponseModel.class,
//    notes = "this method is used to remove users from specific cloud package"
//  )
  public ResponseModel unassign(
      @PathVariable Long id, @RequestBody(required = true) List<Long> users) {
    return cloudPackageService.unassign(id, users);
  }
}
