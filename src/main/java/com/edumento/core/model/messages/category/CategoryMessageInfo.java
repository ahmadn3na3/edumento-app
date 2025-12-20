package com.edumento.core.model.messages.category;

import com.edumento.core.model.SimpleModel;
import com.edumento.core.model.messages.From;

public class CategoryMessageInfo extends SimpleModel {

	private From from;

	public CategoryMessageInfo() {
	}

	public CategoryMessageInfo(Long id, String name, From from) {
		super(id, name);
		this.from = from;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}
}
