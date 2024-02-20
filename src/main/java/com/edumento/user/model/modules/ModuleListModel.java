package com.edumento.user.model.modules;

import com.edumento.user.domain.Module;

public class ModuleListModel extends ModuleCreateModel {

	private Long id;

	public ModuleListModel() {
		// TODO Auto-generated constructor stub
	}

	public ModuleListModel(Long id, String name, String description, String key) {
		super(name, description, key);
		this.id = id;
	}

	public ModuleListModel(Module module) {
		super(module.getName(), module.getDescription(), module.getKeyCode());
		id = module.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
