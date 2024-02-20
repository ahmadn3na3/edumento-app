package com.edumento.category.model;

import java.util.LinkedList;
import java.util.Objects;

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
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		var other = (Grade) obj;
		if (name == null) {
			return other.name == null;
		} else {
			return name.equals(other.name);
		}
	}
}
