package com.edumento.content.domain;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.edumento.core.constants.ContentType;
import com.edumento.core.constants.TaskStatus;
import com.edumento.core.constants.TaskType;
import com.edumento.core.security.SecurityUtils;

/** Created by ahmad on 6/22/16. */
@Document(collection = "tasks")
public class Task {

	@Id
	private String id;
	@Indexed
	private Long contentId;
	@Indexed
	private String userName = SecurityUtils.getCurrentUserLogin();
	private Date startDate = new Date();
	private Date finishedDate;
	private Date expiryDate;
	private String fileName;
	private String folderName;
	private String ext;
	private Long spaceId;
	private Long contentLength;
	private ContentType contentType;
	@Indexed
	private TaskType type;
	@Indexed
	private TaskStatus status;
	private String viewFileName;
	private String viewFolderName;
	private List<Chunk> chunkList = new ArrayList<>();
	private String key;
	private String keyId;

	public Task() {
		var calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		expiryDate = calendar.getTime();
	}

	public Task(Content contentModel) {
		this();
		contentId = contentModel.getId();
		fileName = contentModel.getFileName();
		spaceId = contentModel.getSpace().getId();
		ext = contentModel.getExt().toLowerCase();
		contentLength = contentModel.getSize();
		folderName = contentModel.getFolderName();
		contentType = contentModel.getType();
	}

	public Task(Content contentModel, String viewFolderName, String viewfileName) {
		this(contentModel);
		viewFileName = viewfileName;
		this.viewFolderName = viewFolderName;
		type = TaskType.VEIW;
		status = TaskStatus.OPEN;
		key = contentModel.getKey();
		keyId = contentModel.getKeyId();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getFinishedDate() {
		return finishedDate;
	}

	public void setFinishedDate(Date finishedDate) {
		this.finishedDate = finishedDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Long getContentId() {
		return contentId;
	}

	public void setContentId(Long contentId) {
		this.contentId = contentId;
	}

	public TaskType getType() {
		return type;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
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

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getViewFileName() {
		return viewFileName;
	}

	public void setViewFileName(String viewFileName) {
		this.viewFileName = viewFileName;
	}

	public String getViewFolderName() {
		return viewFolderName;
	}

	public void setViewFolderName(String viewFolderName) {
		this.viewFolderName = viewFolderName;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public List<Chunk> getChunkList() {
		return chunkList;
	}

	public void setChunkList(List<Chunk> chunkList) {
		this.chunkList = chunkList;
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
}
