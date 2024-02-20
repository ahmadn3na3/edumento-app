package com.edumento.user.domain;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.domain.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/** Created by ahmad on 2/27/17. */
@Entity
@DynamicInsert
@DynamicUpdate
public class Module extends AbstractEntity {

	@OneToMany(mappedBy = "module", fetch = FetchType.EAGER)
	Set<Permission> permissions = new HashSet<>();

	// @ManyToMany(mappedBy = "modules")
	// Set<FoundationPackage> foundationPackages = new HashSet<>();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column
	private String name;
	@Column
	private String description;

	@Column(unique = true)
	private String keyCode;

	public Module() {
	}

	public Module(String name, String description, String keyCode) {
		this.name = name;
		this.description = description;
		this.keyCode = keyCode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String key) {
		keyCode = key;
	}

	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass() || !super.equals(o)) {
			return false;
		}

		var module = (Module) o;

		if (!name.equals(module.name)) {
			return false;
		}
		return keyCode.equals(module.keyCode);
	}

	@Override
	public int hashCode() {
		var result = super.hashCode();
		result = 31 * result + name.hashCode();
		return 31 * result + keyCode.hashCode();
	}
}
