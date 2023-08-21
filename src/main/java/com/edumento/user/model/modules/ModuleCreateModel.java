package com.edumento.user.model.modules;

public class ModuleCreateModel {
  private String name;
  private String description;
  private String key;

  public ModuleCreateModel() {
    // TODO Auto-generated constructor stub
  }

  public ModuleCreateModel(String name, String description, String key) {
    super();
    this.name = name;
    this.description = description;
    this.key = key;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
