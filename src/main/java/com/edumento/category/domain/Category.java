package com.edumento.category.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.core.domain.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

/** Created by ahmad on 3/13/16. */
@Entity()
@DynamicInsert
@DynamicUpdate
public class Category extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = true)
	private String nameAr;

	@Column
	private String color;
	@Column
	private String image;
	@Column
	private String thumbnail;

	@ManyToOne
	@JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "FK_CAT_PARENT"))
	private Category parentCategory;

	@OneToMany(mappedBy = "parentCategory")
	private Set<Category> childCategories = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "organization", foreignKey = @ForeignKey(name = "FK_CAT_ORG"))
	private Organization organization;

	@ManyToOne
	@JoinColumn(name = "foundation_id", foreignKey = @ForeignKey(name = "FK_CAT_FOUND"))
	private Foundation foundation;

	public Category() {
	}

	public Category(String name, String color) {
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public Foundation getFoundation() {
		return foundation;
	}

	public void setFoundation(Foundation foundation) {
		this.foundation = foundation;
	}

	public Category getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(Category parentCategory) {
		this.parentCategory = parentCategory;
	}

	public Set<Category> getChildCategories() {
		return childCategories;
	}

	public void setChildCategories(Set<Category> childCategories) {
		this.childCategories = childCategories;
	}

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		var category = (Category) o;

		if (!Objects.equals(id, category.id) || !name.equals(category.name)
				|| !Objects.equals(organization, category.organization)) {
			return false;
		}
		return Objects.equals(foundation, category.foundation);
	}

	@Override
	public int hashCode() {
		var result = id != null ? id.hashCode() : 0;
		result = 31 * result + name.hashCode();
		result = 31 * result + (organization != null ? organization.hashCode() : 0);
		return 31 * result + (foundation != null ? foundation.hashCode() : 0);
	}
}
