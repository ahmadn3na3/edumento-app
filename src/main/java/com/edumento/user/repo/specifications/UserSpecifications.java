package com.edumento.user.repo.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

/** Created by ayman on 16/06/16. */
public final class UserSpecifications {

  private UserSpecifications() {}

  public static Specification<User> hasUserType(UserType... userType) {
    return (root, user, cb) -> root.<UserType>get("type").in(userType);
  }

  public static Specification<User> hasRole(Role role) {
    return (root, user, cb) -> cb.equal(root.<Role>get("roles"), role);
  }

  public static Specification<User> notDeleted() {
    return (root, user, cb) -> cb.equal(root.get("deleted"), false);
  }

  public static Specification<User> inOrganization(Organization organization) {
    return (root, user, cb) -> cb.equal(root.<Organization>get("organization"), organization);
  }

  public static Specification<User> inFoundation(Foundation foundation) {
    return (root, user, cb) -> cb.equal(root.<Organization>get("foundation"), foundation);
  }
}
