package com.edumento.b2b.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.edumento.core.domain.AbstractEntity;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;

/** Created by ahmad on 3/23/16. */
@Entity
@DynamicInsert
@DynamicUpdate
public class Role extends AbstractEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String name;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "permissions", joinColumns = @JoinColumn(name = "role"), foreignKey = @ForeignKey(name = "FK_ROLE_PERMISSION"))
  @MapKeyColumn(name = "permission_name")
  private Map<String, Byte> permission = new HashMap<>();

  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinTable(name = "role_users", joinColumns = {
      @JoinColumn(name = "roles", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_ROLE_USER"))
  }, inverseJoinColumns = {
      @JoinColumn(name = "users", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_USER_ROLE"))
  })
  private Set<User> users = new HashSet<>();

  @Enumerated
  private UserType type;

  @ManyToOne
  @JoinColumn(name = "organization", foreignKey = @ForeignKey(name = "FK_ROLE_ORGANIZATION"))
  private Organization organization;

  @ManyToOne
  @JoinColumn(name = "foundation_id", foreignKey = @ForeignKey(name = "FK_ROLE_FOUNDATION"))
  private Foundation foundation;

  public Role() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Byte> getPermission() {
    return permission;
  }

  public void setPermission(Map<String, Byte> permission) {
    this.permission = permission;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<User> getUsers() {
    return users;
  }

  public void setUsers(Set<User> users) {
    this.users = users;
  }

  public Organization getOrganization() {
    return organization;
  }

  public void setOrganization(Organization organization) {
    this.organization = organization;
  }

  public Foundation getFoundation() {
    return foundation;
  }

  public void setFoundation(Foundation foundation) {
    this.foundation = foundation;
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
    if (o == null || getClass() != o.getClass()) {
		return false;
	}

    Role role = (Role) o;

    if (!getId().equals(role.getId())) {
		return false;
	}
    return getName().equals(role.getName());
  }

  @Override
  public int hashCode() {
    int result = getId().hashCode();
    result = 31 * result + getName().hashCode();
    return result;
  }
}
