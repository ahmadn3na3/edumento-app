package com.edumento.core.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/** Utility class for Spring Security. */
public final class SecurityUtils {

  private SecurityUtils() {}

  /** Get the login of the current user. */
  public static String getCurrentUserLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    String userName = null;
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
          userName = springSecurityUser.getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
        userName = (String) authentication.getPrincipal();
      }
    }
    return userName;
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise
   */
  public static boolean isAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Collection<? extends GrantedAuthority> authorities =
        securityContext.getAuthentication().getAuthorities();
    if (authorities != null) {
      for (GrantedAuthority authority : authorities) {
        if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Return the current user, or throws an exception, if the user is not authenticated yet.
   *
   * @return the current user
   */
  public static CurrentUserDetail getCurrentUser() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof CurrentUserDetail) {
        return (CurrentUserDetail) authentication.getPrincipal();
      }
    }
    return null;
  }

  /**
   * If the current user has a specific authority (security role).
   *
   * <p>
   *
   * <p>
   * The name of this method comes from the isUserInRole() method in the Servlet API
   */
  public static boolean isCurrentUserInRole(String authority) {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication != null) {
      if (!authentication.getAuthorities().isEmpty()) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(authority));
      }
      if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
          return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(authority));
      }
    }
    return false;
  }
}
