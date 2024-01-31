package com.edumento.auth.security;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.edumento.core.security.CurrentUserDetail;
import com.edumento.core.util.PermissionCheck;
import com.edumento.user.constant.UserType;
import com.edumento.user.domain.Permission;
import com.edumento.user.domain.User;
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

/** Authenticate a user from the database. */
@SuppressWarnings("Convert2MethodRef")
@Component("userDetailsService")
public class EDumentoUserDetailsService implements UserDetailsService {

  private final Logger log;
  private final PermissionRepository permissionRepository;
  private final UserRepository userRepository;
  private final MessageSource messageSource;


  public EDumentoUserDetailsService(PermissionRepository permissionRepository,
      UserRepository userRepository, MessageSource messageSource) {
    this.log = LoggerFactory.getLogger(UserDetailsService.class);
    this.permissionRepository = permissionRepository;
    this.userRepository = userRepository;
    this.messageSource = messageSource;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(final String login) {
    log.debug("Authenticating {}", login);
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String lowercaseLogin = login.toLowerCase();
    Optional<User> userFromDatabase =
        userRepository.findOneByUserNameAndDeletedFalse(lowercaseLogin);
    if (!userFromDatabase.isPresent()) {
      userFromDatabase = userRepository.findOneByEmailAndDeletedFalse(lowercaseLogin);
    }
    if (!userFromDatabase.isPresent()) {
      throw new UsernameNotFoundException(
          "User " + lowercaseLogin + " was not found in the " + "database");
    }
    User user = userFromDatabase.get();
    if (user.getEndDate() != null && user.getEndDate().before(new Date())) {
      throw new UserExpiredException("User " + lowercaseLogin + " is Expired");
    }

    var locale = Locale.forLanguageTag(
        request != null && request.getHeader("lang") != null ? request.getHeader("lang") : "en");
    if (locale == null) {
      locale = Locale.ENGLISH;
    }
    if (Boolean.FALSE.equals(user.getStatus())) {
      if (user.getOrganization() != null || user.getFoundation() != null) {
        String message = messageSource.getMessage("error.account.disable.b2b", null,
            "account not active", locale);
        throw new UserDisabledException(message);
      }
      if (user.getCloudPackage() != null && user.getFirstLogin() == Boolean.FALSE) {
        String message = messageSource.getMessage("error.account.disable.b2c", null,
            "account not active", locale);
        throw new UserDisabledException(message);
      }
      throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
    }
    Set<String> permissions = new HashSet<>();
    List<Permission> permissionList = permissionRepository.findAll();
    switch (user.getType()) {
      case SUPER_ADMIN:
        permissionList.forEach(permission -> permissions.add(permission.getName()));
        permissions.add(UserType.SUPER_ADMIN.getAuthority());
        permissions.add(UserType.SYSTEM_ADMIN.getAuthority());
        permissions.add(UserType.FOUNDATION_ADMIN.getAuthority());
        permissions.add(UserType.ADMIN.getAuthority());
        break;
      case SYSTEM_ADMIN:
        permissionRepository
            .findByTypeInAndDeletedFalse(Arrays.asList(UserType.SYSTEM_ADMIN,
                UserType.FOUNDATION_ADMIN, UserType.ADMIN, UserType.USER))
            .forEach(permission -> permissions.add(permission.getName()));
        permissions.add(UserType.SYSTEM_ADMIN.getAuthority());
        permissions.add(UserType.FOUNDATION_ADMIN.getAuthority());
        permissions.add(UserType.ADMIN.getAuthority());
        break;
      case USER:
        if (user.getCloudPackage() != null) {
          user.getCloudPackage().getPermission()
              .forEach((s, o) -> permissions.addAll(get(permissionList, s, o)));
        } else {
          user.getRoles().stream().forEach(role -> role.getPermission()
              .forEach((s, o) -> permissions.addAll(get(permissionList, s, o))));
        }
        break;
      case FOUNDATION_ADMIN:
        permissions.add(UserType.ADMIN.getAuthority());
        permissions.add(UserType.FOUNDATION_ADMIN.getAuthority());
        user.getRoles().stream().forEach(role -> role.getPermission()
            .forEach((s, o) -> permissions.addAll(get(permissionList, s, o))));
        break;
      case ADMIN:
        user.getRoles().stream().forEach(role -> role.getPermission()
            .forEach((s, o) -> permissions.addAll(get(permissionList, s, o))));
        permissions.add(UserType.ADMIN.getAuthority());
        break;
    }
    Long org = user.getOrganization() == null ? null : user.getOrganization().getId();
    Long found = user.getFoundation() == null ? null : user.getFoundation().getId();
    return new CurrentUserDetail(user.getId(), user.getUserName().toLowerCase(), user.getPassword(),
        AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", permissions)),
        user.getFullName(), user.getThumbnail(), user.getEmail(), org, found, user.getType(),
        user.getChatId());
  }

  private Set<String> get(List<Permission> permissions, String name, byte equation) {
    return permissions.stream()
        .filter(permission -> permission.getKeyCode().equals(name)
            && PermissionCheck.hasAction(equation, permission.getCode().byteValue()))
        .map(Permission::getName).collect(Collectors.toSet());
  }
}
