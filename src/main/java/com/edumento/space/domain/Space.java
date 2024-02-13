package com.edumento.space.domain;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.category.domain.Category;
import com.edumento.content.domain.Content;
import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.domain.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/** Created by ahmad on 3/2/16. */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(indexes = { @Index(columnList = "name", name = "name_index"),
    @Index(columnList = "description", name = "description_index") })
public class Space extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column
  private String color;

  @Column(nullable = false)
  private String name;

  @Column(length = 4000)
  private String objective;

  @Column
  private Double rating;
  @Column
  private Double price;
  @Column
  private Boolean paid = Boolean.FALSE;
  @Column
  private Boolean isPrivate = Boolean.FALSE;
  @Column
  private String image;
  @Column
  private String thumbnail;
  @Column
  private String chatRoomId;

  @Column(length = 600)
  private String description;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SPACE_CATEGORY"))
  private Category category;

  @ManyToOne
  @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "FK_SPACE_OWNER"))
  private User user;

  @Column
  private Boolean joinRequestsAllowed = Boolean.FALSE;
  @Column
  private Boolean autoWifiSyncAllowed = Boolean.FALSE;
  @Column
  private Boolean showCommunity = Boolean.FALSE;

  @Column
  private Boolean allowLeave = Boolean.TRUE;

  @Column
  private Boolean allowRecommendation = Boolean.FALSE;

  @OneToMany(mappedBy = "space", cascade = CascadeType.REMOVE)
  private List<Joined> joinedList = new ArrayList<>();

  @OneToMany(mappedBy = "space", cascade = CascadeType.ALL)
  private List<Content> contents = new ArrayList<>();

  public Space() {
  }

  public Space(String name, String objective, Double price, Boolean paid, Boolean isPrivate,
      String image, String description) {
    this.name = name;
    this.objective = objective;
    this.price = price;
    this.paid = paid;
    this.isPrivate = isPrivate;
    this.image = image;
    this.description = description;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getObjective() {
    return objective;
  }

  public void setObjective(String objective) {
    this.objective = objective;
  }

  public Double getRating() {
    return rating;
  }

  public void setRating(Double rating) {
    this.rating = rating;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public Boolean getIsPrivate() {
    return isPrivate;
  }

  public void setIsPrivate(Boolean aPrivate) {
    isPrivate = aPrivate;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getPaid() {
    return paid;
  }

  public void setPaid(Boolean paid) {
    this.paid = paid;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Boolean getJoinRequestsAllowed() {
    return joinRequestsAllowed;
  }

  public void setJoinRequestsAllowed(Boolean joinRequestsAllowed) {
    this.joinRequestsAllowed = joinRequestsAllowed;
  }

  public Boolean getAutoWifiSyncAllowed() {
    return autoWifiSyncAllowed;
  }

  public void setAutoWifiSyncAllowed(Boolean autoWifiSyncAllowed) {
    this.autoWifiSyncAllowed = autoWifiSyncAllowed;
  }

  public Boolean getShowCommunity() {
    return showCommunity;
  }

  public void setShowCommunity(Boolean showCommunity) {
    this.showCommunity = showCommunity;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getThumbnail() {
    return thumbnail;
  }

  public void setThumbnail(String thumbnail) {
    this.thumbnail = thumbnail;
  }

  public List<Joined> getJoinedList() {
    return joinedList;
  }

  public void setJoinedList(List<Joined> joinedList) {
    this.joinedList = joinedList;
  }

  public Boolean getAllowRecommendation() {
    return allowRecommendation;
  }

  public void setAllowRecommendation(Boolean allowRecommendation) {
    this.allowRecommendation = allowRecommendation;
  }

  public List<Content> getContents() {
    return contents;
  }

  public void setContents(List<Content> contents) {
    this.contents = contents;
  }

  public Boolean getAllowLeave() {
    return allowLeave;
  }

  public void setAllowLeave(Boolean allowLeave) {
    this.allowLeave = allowLeave;
  }

  public String getChatRoomId() {
    return chatRoomId;
  }

  public void setChatRoomId(String chatRoomId) {
    this.chatRoomId = chatRoomId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Space space = (Space) o;

    if ((id != null ? !id.equals(space.id) : space.id != null) || !name.equals(space.name) || !category.equals(space.category)) {
      return false;
    }
    return user.equals(space.user);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + name.hashCode();
    result = 31 * result + category.hashCode();
    result = 31 * result + user.hashCode();
    return result;
  }
}
