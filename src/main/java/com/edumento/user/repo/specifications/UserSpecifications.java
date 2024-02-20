package com.edumento.user.repo.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

import com.edumento.b2b.domain.Foundation;
import com.edumento.b2b.domain.Organization;
import com.edumento.b2b.domain.Role;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/** Created by ayman on 16/06/16. */
public final class UserSpecifications {

	private UserSpecifications() {
	}

	public static Specification<User> hasUserType(UserType... userType) {
		return new Specification<User>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<User> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return root.<UserType>get("type").in(userType);
		}
	};
	}

	public static Specification<User> hasRole(Role role) {
		return new Specification<User>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<User> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return cb.equal(root.<Role>get("roles"), role);
		}
	};
	}

	public static Specification<User> notDeleted() {
		return new Specification<User>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<User> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return cb.equal(root.get("deleted"), false);
		}
	};
	}

	public static Specification<User> inOrganization(Organization organization) {
		return new Specification<User>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<User> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return cb.equal(root.<Organization>get("organization"), organization);
		}
	};
	}

	public static Specification<User> inFoundation(Foundation foundation) {
		return new Specification<User>() {
		@Override
		@Nullable
		public Predicate toPredicate(Root<User> root, CriteriaQuery<?> user, CriteriaBuilder cb) {
			return cb.equal(root.<Organization>get("foundation"), foundation);
		}
	};
	}
}
