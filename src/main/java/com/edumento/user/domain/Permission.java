package com.edumento.user.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.constant.UserType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Objects;

/** Created by ahmad on 3/2/17. */
@Entity
@Table(name = "module_permissions")
@DynamicInsert
@DynamicUpdate
public class Permission extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(unique = true, nullable = false)
  private String name;

  @Column(unique = false, nullable = false)
  private String keyCode;

  @Column
  private Integer code;

  @Column
  @Enumerated(EnumType.STRING)
  private UserType type;

  @ManyToOne
  @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_MODULE"))
  private Module module;

  public Permission() {}

  public Permission(String name, String keyCode, Integer code, UserType type, Module module) {
    this.name = name;
    this.keyCode = keyCode;
    this.code = code;
    this.type = type;
    this.module = module;
  }

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

  public String getKeyCode() {
    return keyCode;
  }

  public void setKeyCode(String keyCode) {
    this.keyCode = keyCode;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public UserType getType() {
    return type;
  }

  public void setType(UserType type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
		return true;
	}
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
		return false;
	}

    Permission that = (Permission) o;

    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
