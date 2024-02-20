package com.edumento.content.models;

import java.util.ArrayList;
import java.util.List;

/** Created by ahmad on 7/3/16. */
public class ContentUpdateModel {
	private List<ContentModel> newContents = new ArrayList<>();
	private List<ContentModel> updatedContents = new ArrayList<>();
	private List<Long> deletedContents = new ArrayList<>();

	public List<ContentModel> getNewContents() {
		return newContents;
	}

	public void setNewContents(List<ContentModel> newContents) {
		this.newContents = newContents;
	}

	public List<ContentModel> getUpdatedContents() {
		return updatedContents;
	}

	public void setUpdatedContents(List<ContentModel> updatedContents) {
		this.updatedContents = updatedContents;
	}

	public List<Long> getDeletedContents() {
		return deletedContents;
	}

	public void setDeletedContents(List<Long> deletedContents) {
		this.deletedContents = deletedContents;
	}

	@Override
	public String toString() {
		return "ContentUpdateModel{" + "newContents=" + newContents + ", updatedContents=" + updatedContents
				+ ", deletedContents=" + deletedContents + '}';
	}
}
