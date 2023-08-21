package com.edumento.b2b.model.timelock;

public class TimeLockExceptionModel extends TimeLockExceptionCreationModel {
  private Long id;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return "TimeLockExceptionModel{" + "id=" + id + "} " + super.toString();
  }
}
