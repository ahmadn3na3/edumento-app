package com.edumento.category.model;

import java.util.LinkedList;

public class Grade {
  private LinkedList<Chapter> chapters = new LinkedList<>();
  private String name;

  public LinkedList<Chapter> getChapters() {
    return chapters;
  }

  public void setChapters(LinkedList<Chapter> chapters) {
    this.chapters = chapters;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
    if (this == obj) {
		return true;
	}
    if ((obj == null) || (getClass() != obj.getClass())) {
		return false;
	}
    Grade other = (Grade) obj;
    if (name == null) {
      if (other.name != null) {
		return false;
	}
    } else if (!name.equals(other.name)) {
		return false;
	}
    return true;
  }
}
