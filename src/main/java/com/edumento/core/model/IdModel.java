package com.edumento.core.model;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 5/15/16. */
public class IdModel {

  @NotNull
  private Long id;

  public IdModel() {}

  public IdModel(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "IdModel{" + "id=" + id + '}';
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
		return true;
	}
    if ((obj == null) || (getClass() != obj.getClass())) {
		return false;
	}
    IdModel other = (IdModel) obj;
    if (id == null) {
        return other.id == null;
    } else return id.equals(other.id);
  }
}
