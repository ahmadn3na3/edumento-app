package com.edumento.user.model.modules;

import java.util.HashSet;
import java.util.Set;

import com.edumento.user.domain.Module;

public class ModuleModel extends ModuleListModel {
	private Set<PermissionModel> permissions = new HashSet<>();

	public ModuleModel() {
	}

	public ModuleModel(Long id, String name, String description, String key) {
		super(id, name, description, key);
	}

	public ModuleModel(Module module) {
		super(module.getId(), module.getName(), module.getDescription(), module.getKeyCode());
	}

	public Set<PermissionModel> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<PermissionModel> permissions) {
		this.permissions = permissions;
	}
}
