package com.edumento.category.model;

import com.edumento.b2b.model.organization.SimpleOrganizationModel;
import com.edumento.core.model.SimpleModel;

/** Created by ahmad on 3/13/16. */
public class CategoryModel extends CreateCategoryModel {

	private Long id;
	private SimpleOrganizationModel organization;
	private SimpleModel foundation;
	private SimpleModel parentCategory;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SimpleOrganizationModel getOrganization() {
		return organization;
	}

	public void setOrganization(SimpleOrganizationModel organization) {
		this.organization = organization;
	}

	public SimpleModel getFoundation() {
		return foundation;
	}

	public void setFoundation(SimpleModel foundation) {
		this.foundation = foundation;
	}

	public SimpleModel getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(SimpleModel parentCategory) {
		this.parentCategory = parentCategory;
	}

	@Override
	public String toString() {
		return "CategoryModel{" + "id=" + id + ", organization=" + organization + ", foundation=" + foundation + "} "
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
