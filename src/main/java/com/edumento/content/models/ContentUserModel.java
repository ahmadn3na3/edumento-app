package com.edumento.content.models;

import com.edumento.user.domain.User;

/** Created by ahmad on 7/12/16. */
public class ContentUserModel {
  private Long id;

  private String userName;

  private String name;

  private String image;

  public ContentUserModel() {}

  public ContentUserModel(User user) {
    this.setId(user.getId());
    this.setUserName(user.getUserName());
    this.setImage(user.getThumbnail());
    this.setName(user.getFullName());
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public String toString() {
    return "ContentUserModel{"
        + "id="
        + id
        + ", userName='"
        + userName
        + '\''
        + ", name='"
        + name
        + '\''
        + ", image='"
        + image
        + '\''
        + '}';
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof ContentUserModel)) {
      return false;
    }
    ContentUserModel other = (ContentUserModel) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }
}
