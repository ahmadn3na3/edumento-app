package com.edumento.core.model.messages;

import java.util.Objects;

/** Created by ayman on 22/01/17. */
public class Target {

  private int type;
  private String id;
  private int action;
  private String image;

  public Target() {}

  public Target(int type, String id, int action) {
    this.type = type;
    this.id = id;
    this.action = action;
  }

  public Target(int type, String id, int action, String image) {
    super();
    this.type = type;
    this.id = id;
    this.action = action;
    this.image = image;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Target target = (Target) o;

    if (type != target.type) return false;
    if (action != target.action) return false;
    return Objects.equals(id, target.id);
  }

  @Override
  public int hashCode() {
    int result = type;
    result = 31 * result + (id != null ? id.hashCode() : 0);
    result = 31 * result + action;
    return result;
  }

  @Override
  public String toString() {
    return String.format("Target [type=%s, id=%s, action=%s, image=%s]", type, id, action, image);
  }
}
