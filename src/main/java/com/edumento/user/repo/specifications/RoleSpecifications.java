package com.edumento.user.repo.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.user.constant.UserType;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/** Created by ayman on 21/06/16. */
public final class RoleSpecifications {
	private RoleSpecifications() {
	}

	public static Specification<Role> notDeleted() {
		return new Specification<Role>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return cb.equal(root.get("deleted"), false);
		}
	};
	}

	public static Specification<Role> hasType(UserType usertype) {
		return new Specification<Role>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> role, CriteriaBuilder cb) {
			return cb.equal(root.<UserType>get("type"), usertype);
		}
	};
	}

	public static Specification<Role> hasType(UserType... usertype) {
		return new Specification<Role>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> role, CriteriaBuilder cb) {
			return root.get("type").in(usertype);
		}
	};
	}

	public static Specification<Role> byOrganization(Organization organization) {
		return new Specification<Role>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> role, CriteriaBuilder cb) {
			return cb.equal(root.get("organization"), organization);
		}
	};
	}

	public static Specification<Role> byFoundation(Foundation foundation) {
		return new Specification<Role>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> role, CriteriaBuilder cb) {
			return cb.equal(root.get("foundation"), foundation);
		}
	};
	}
}
