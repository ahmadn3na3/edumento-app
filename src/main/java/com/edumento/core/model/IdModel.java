package com.edumento.core.model;

import java.util.Objects;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 5/15/16. */
public class IdModel {

	@NotNull
	private Long id;

	public IdModel() {
	}

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
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		var other = (IdModel) obj;
		if (id == null) {
			return other.id == null;
		} else {
			return id.equals(other.id);
		}
	}
}
