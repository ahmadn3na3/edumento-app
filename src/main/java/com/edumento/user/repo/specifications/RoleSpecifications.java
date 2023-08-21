package com.edumento.user.repo.specifications;

import org.springframework.data.jpa.domain.Specification;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.user.constant.UserType;

/** Created by ayman on 21/06/16. */
public final class RoleSpecifications {
  private RoleSpecifications() {}

  public static Specification<Role> notDeleted() {
    return (root, user, cb) -> cb.equal(root.get("deleted"), false);
  }

  public static Specification<Role> hasType(UserType usertype) {
    return (root, role, cb) -> cb.equal(root.<UserType>get("type"), usertype);
  }

  public static Specification<Role> hasType(UserType... usertype) {
    return (root, role, cb) -> root.get("type").in(usertype);
  }

  public static Specification<Role> byOrganization(Organization organization) {
    return (root, role, cb) -> cb.equal(root.get("organization"), organization);
  }

  public static Specification<Role> byFoundation(Foundation foundation) {
    return (root, role, cb) -> cb.equal(root.get("foundation"), foundation);
  }
}
