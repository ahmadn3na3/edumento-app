package com.edumento.core.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.edumento.core.exception.NotPermittedException;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.User;

/** Created by ahmad on 5/18/16. */
public final class PermissionCheck {
	public static boolean checkPermission(String permission) {
		var context = SecurityContextHolder.getContext();
		if (context == null) {
			return false;
		}

		var authentication = context.getAuthentication();
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

	public static boolean hasAction(byte equation, byte action) {
		return (equation & action) == action;
	}
}
