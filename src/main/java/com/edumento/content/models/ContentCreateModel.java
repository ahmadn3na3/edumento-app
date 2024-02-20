package com.edumento.content.models;

import java.util.ArrayList;
import java.util.List;

import com.edumento.core.constants.ContentType;

import jakarta.validation.constraints.NotNull;

/** Created by ahmad on 7/2/16. */
public class ContentCreateModel {
	@NotNull(message = "error.content.name")
	private String name;

	private String shelf;

	private String checkSum;

	@NotNull(message = "error.content.space")
	private Long spaceId;

	private Long contentLength;

	private String ext;

	private List<String> tags = new ArrayList<>();

	private ContentType type;

	private String thumbnail;

	private String contentUrl;

	private Boolean allowUseOriginal = Boolean.FALSE;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShelf() {
		return shelf;
	}

	public void setShelf(String shelf) {
		this.shelf = shelf;
	}

	public Long getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(Long spaceId) {
		this.spaceId = spaceId;
	}

	public Long getContentLength() {
		return contentLength;
	}

	public void setContentLength(Long contentLength) {
		this.contentLength = contentLength;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public ContentType getType() {
		return type;
	}

	public void setType(ContentType type) {
		this.type = type;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contnetUrl) {
		contentUrl = contnetUrl;
	}

	public Boolean getAllowUseOriginal() {
		return allowUseOriginal;
	}

	public void setAllowUseOriginal(Boolean allowUseOrginal) {
		allowUseOriginal = allowUseOrginal;
	}

	@Override
	public String toString() {
		return "ContentCreateModel{" + "name='" + name + '\'' + ", shelf='" + shelf + '\'' + ", checkSum='" + checkSum
				+ '\'' + ", spaceId=" + spaceId + ", contentLength=" + contentLength + ", ext='" + ext + '\''
				+ ", tags=" + tags + ", type=" + type + ", thumbnail='" + thumbnail + '\'' + '}';
	}
}
