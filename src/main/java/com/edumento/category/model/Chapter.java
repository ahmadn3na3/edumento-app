package com.edumento.category.model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/** Created by ahmad on 7/14/16. */
public class Chapter {
  private String name;
  private LinkedList<String> sections = new LinkedList<>();

  public Chapter() {}

  public Chapter(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LinkedList<String> getSections() {
    return sections;
  }

  public void setSections(LinkedList<String> sections) {
    this.sections = sections;
  }

  @Override
  public String toString() {
    return "Chapter{" + "name='" + name + '\'' + ", sections=" + sections + '}';
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Chapter other = (Chapter) obj;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    return true;
  }
}
