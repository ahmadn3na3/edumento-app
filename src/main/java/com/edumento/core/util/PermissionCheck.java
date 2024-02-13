package com.edumento.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.edumento.core.exception.NotPermittedException;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

/** Created by ahmad on 5/18/16. */
public final class PermissionCheck {
  public static boolean checkPermission(String permission) {
    SecurityContext context = SecurityContextHolder.getContext();
    if (context == null) {
		return false;
	}

    Authentication authentication = context.getAuthentication();
    if (authentication == null) {
		return false;
	}

    for (GrantedAuthority auth : authentication.getAuthorities()) {
      if (permission.equals(auth.getAuthority())) {
		return true;
	}
    }

    return false;
  }

  public static void checkUserForFoundationAndOrgOperation(User user, Long organizationId,
      Long foundationId) {
    if (user.getType() == UserType.FOUNDATION_ADMIN && foundationId == null) {
      throw new NotPermittedException();
    }
    if (user.getType() == UserType.ADMIN && foundationId == null && organizationId == null) {
      throw new NotPermittedException();
    }
    if (user.getType() != UserType.SUPER_ADMIN && user.getType() != UserType.SYSTEM_ADMIN) {
      if ((user.getType() == UserType.FOUNDATION_ADMIN
          && !user.getFoundation().getId().equals(foundationId))
          || (user.getType() == UserType.ADMIN
              && !user.getOrganization().getId().equals(organizationId))
          || user.getType() == UserType.USER) {
        throw new NotPermittedException();
      }
    }
  }

  public static boolean hasAction(byte equation, byte action) {
    return (equation & action) == action;
  }
}
