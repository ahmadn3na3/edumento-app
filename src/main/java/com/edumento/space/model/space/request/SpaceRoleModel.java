/** */
package com.edumento.space.model.space.request;

import com.edumento.core.constants.SpaceRole;

/** @author ahmad */
public class SpaceRoleModel {
  private Long id;
  private String name;
  private SpaceRole role = SpaceRole.VIEWER;

  public SpaceRoleModel() {
    // TODO Auto-generated constructor stub
  }

  public SpaceRoleModel(String name) {
    this();
    this.name = name;
  }

  public SpaceRoleModel(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public SpaceRole getRole() {
    return role;
  }

  public void setRole(SpaceRole role) {
    this.role = role;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SpaceRoleModel other = (SpaceRoleModel) obj;
    return !(id == null || !id.equals(other.id));
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("SpaceRoleModel [name=%s, role=%s]", name, role);
  }
}
