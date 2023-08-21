package com.edumento.user.model.modules;

import com.edumento.user.domain.Permission;

public class PermissionModel extends PermissionCreateModel {
  private Long id;

  public PermissionModel() {
    // TODO Auto-generated constructor stub
  }

  public PermissionModel(Permission permission) {
    super(permission.getName(), permission.getKeyCode(), permission.getCode());
    this.id = permission.getId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
}
