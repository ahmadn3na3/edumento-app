package com.edumento.category.model;

import com.edumento.core.model.SimpleModel;

public class CategoryModel extends CreateCategoryModel {

	private Long id;
	private SimpleModel parentCategory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SimpleModel getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(SimpleModel parentCategory) {
		this.parentCategory = parentCategory;
	}

	@Override
	public String toString() {
		return "CategoryModel{" + "id=" + id + "} "
				+ super.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var that = (CategoryModel) o;

		return id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
