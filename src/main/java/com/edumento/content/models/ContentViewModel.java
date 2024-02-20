package com.edumento.content.models;

import java.util.HashSet;
import java.util.Set;

/** Created by ahmad on 11/23/16. */
public class ContentViewModel {
	private String viewTaskId;
	private Set<String> urls = new HashSet<>();
	private String key;
	private String keyId;
	private String encodedKey;
	private String encodedKeyId;
	private Long size;

	public String getViewTaskId() {
		return viewTaskId;
	}

	public void setViewTaskId(String viewTaskId) {
		this.viewTaskId = viewTaskId;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKeyId() {
		return keyId;
	}

	public void setKeyId(String keyId) {
		this.keyId = keyId;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}

	public String getEncodedKeyId() {
		return encodedKeyId;
	}

	public void setEncodedKeyId(String encodedKeyId) {
		this.encodedKeyId = encodedKeyId;
	}

	@Override
	public String toString() {
		return "ContentViewModel{" + "viewTaskId='" + viewTaskId + '\'' + ", urls=" + urls + ", key='" + key + '\''
				+ ", keyId='" + keyId + '\'' + '}';
	}
}
