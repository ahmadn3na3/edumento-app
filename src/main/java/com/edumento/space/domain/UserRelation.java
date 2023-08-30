package com.edumento.space.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.constants.UserRelationType;
import com.edumento.core.domain.AbstractEntity;
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

@Entity
@DynamicInsert
@DynamicUpdate
public class UserRelation extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_USER"))
  private User user;

  @ManyToOne
  @JoinColumn(name = "follow_id", foreignKey = @ForeignKey(name = "FK_FOLLOWER"))
  private User follow;

  @Enumerated private UserRelationType relationType = UserRelationType.FOLLOWER;

  @Column private String groupName;

  @Column private String reason;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public User getFollow() {
    return follow;
  }

  public void setFollow(User follow) {
    this.follow = follow;
  }

  public UserRelationType getRelationType() {
    return relationType;
  }

  public void setRelationType(UserRelationType relationType) {
    this.relationType = relationType;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }
}
