package com.edumento.user.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.edumento.core.constants.ContentType;
import com.edumento.core.domain.AbstractEntity;

/** Created by ahmad on 3/1/17. */
@Document(collection = "mint.user.resources")
public class UserResources extends AbstractEntity {

	@Id
	private String id;
	@Field
	private Long userId;
	@Field
	private String userName;
	@Field
	private ContentType resourceType;
	@Field
	private String format;
	@Field
	private String fileName;
	@Field
	private Long fileSize;

	@Field
	private String diskFileName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ContentType getResourceType() {
		return resourceType;
	}

	public void setResourceType(ContentType resourceType) {
		this.resourceType = resourceType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getDiskFileName() {
		return diskFileName;
	}

	public void setDiskFileName(String diskFileName) {
		this.diskFileName = diskFileName;
	}
}
