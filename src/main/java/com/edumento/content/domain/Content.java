package com.edumento.content.domain;

import java.util.UUID;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.constants.ContentStatus;
import com.edumento.core.constants.ContentType;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.space.domain.Space;
import com.edumento.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/** Created by ahmad on 6/16/16. */
@Entity
@DynamicInsert
@DynamicUpdate
public class Content extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(length = 50, nullable = false)
  private String name;

  @Column(length = 50, nullable = false)
  private String shelfName;

  @ManyToOne
  @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "FK_CONTENT_OWNER"))
  private User owner;

  @ManyToOne
  @JoinColumn(name = "space_id", foreignKey = @ForeignKey(name = "FK_CONTENT_SPACE"))
  private Space space;

  @Column private String fileName = UUID.randomUUID().toString();

  @Column private String folderName = UUID.randomUUID().toString();
  @Column private String checkSum;

  @Column private Long size;

  @Column(name = "enc_key_id")
  private String keyId;

  @Column(name = "enc_key")
  private String key;

  @Column private String thumbnail;

  @Column @Enumerated private ContentStatus status = ContentStatus.NOT_UPLOAD;

  private String ext;

  @Column(length = 4000)
  private String tags;

  @Column @Enumerated private ContentType type;

  @Column(length = 2083)
  private String contentUrl;

  @Column private String thumbResource;
  @Column private Boolean allowUseOrginal = Boolean.FALSE;
  @Column private Integer numberOfAnnotation = 0;

  @Column private String originalPath;

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

  public String getShelfName() {
    return shelfName;
  }

  public void setShelfName(String shelfName) {
    this.shelfName = shelfName;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Space getSpace() {
    return space;
  }

  public void setSpace(Space space) {
    this.space = space;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getCheckSum() {
    return checkSum;
  }

  public void setCheckSum(String checkSum) {
    this.checkSum = checkSum;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public ContentStatus getStatus() {
    return status;
  }

  public void setStatus(ContentStatus status) {
    this.status = status;
  }

  public String getExt() {
    return ext;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public ContentType getType() {
    return type;
  }

  public void setType(ContentType type) {
    this.type = type;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public String getFolderName() {
    return folderName;
  }

  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getContentUrl() {
    return contentUrl;
  }

  public void setContentUrl(String contentUrl) {
    this.contentUrl = contentUrl;
  }

  public String getThumbResource() {
    return thumbResource;
  }

  public void setThumbResource(String thumbResource) {
    this.thumbResource = thumbResource;
  }

  public Boolean getAllowUseOrginal() {
    return allowUseOrginal;
  }

  public void setAllowUseOrginal(Boolean allowUseOrginal) {
    if (allowUseOrginal == null) {
      return;
    }
    this.allowUseOrginal = allowUseOrginal;
  }

  public Integer getNumberOfAnnotation() {
    return numberOfAnnotation == null ? 0 : numberOfAnnotation;
  }

  public void setNumberOfAnnotation(Integer numberOfAnnotation) {
    if (numberOfAnnotation != null) {
		this.numberOfAnnotation = numberOfAnnotation;
	}
  }

  public String getOriginalPath() {
    return originalPath;
  }

  public void setOriginalPath(String originalPath) {
    this.originalPath = originalPath;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Content content = (Content) o;

    if (!id.equals(content.id) || (name != null ? !name.equals(content.name) : content.name != null) || !shelfName.equals(content.shelfName) || (owner != null ? !owner.equals(content.owner) : content.owner != null)) {
      return false;
    }
    return space != null ? space.equals(content.space) : content.space == null;
  }

  @Override
  public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + shelfName.hashCode();
    result = 31 * result + (owner != null ? owner.hashCode() : 0);
    result = 31 * result + (space != null ? space.hashCode() : 0);
    return result;
  }
}
