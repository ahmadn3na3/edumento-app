package com.edumento.core.model;

/** Created by ahmad on 5/18/16. */
public class SimpleModel extends IdModel {
  private String name;

  public SimpleModel() {}

  public SimpleModel(Long id, String name) {
    super(id);
    this.name = name;
  }

  public SimpleModel(Long id) {
    super(id);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "SimpleModel{" + "name='" + name + '\'' + "} " + super.toString();
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
		return true;
	} else if (!super.equals(obj)) {
		return false;
	} else if (getClass() != obj.getClass()) {
		return false;
	}
    return true;
  }
}
