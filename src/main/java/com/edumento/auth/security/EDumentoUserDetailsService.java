package com.edumento.auth.security;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;

/** Authenticate a user from the database. */
@SuppressWarnings("Convert2MethodRef")
@Component("userDetailsService")
public class EDumentoUserDetailsService implements UserDetailsService {

	private final Logger log;
	private final PermissionRepository permissionRepository;
	private final UserRepository userRepository;

	public EDumentoUserDetailsService(PermissionRepository permissionRepository, UserRepository userRepository) {
		log = LoggerFactory.getLogger(UserDetailsService.class);
		this.permissionRepository = permissionRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(final String login) {
		log.debug("Authenticating {}", login);
		var request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		var lowercaseLogin = login.toLowerCase();
		var userFromDatabase = userRepository.findOneByUserNameAndDeletedFalse(lowercaseLogin);
		if (!userFromDatabase.isPresent()) {
			userFromDatabase = userRepository.findOneByEmailAndDeletedFalse(lowercaseLogin);
		}
		if (!userFromDatabase.isPresent()) {
			throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the " + "database");
		}
		var user = userFromDatabase.get();
		if (user.getEndDate() != null && user.getEndDate().before(new Date())) {
			throw new UserExpiredException("User " + lowercaseLogin + " is Expired");
		}

		var locale = Locale.forLanguageTag(
				request != null && request.getHeader("lang") != null ? request.getHeader("lang") : "en");
		if (locale == null) {
			locale = Locale.ENGLISH;
		}
		if (Boolean.FALSE.equals(user.getStatus())) {
			throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
		}
		Set<String> permissions = new HashSet<>();
		var permissionList = permissionRepository.findAll();

		switch (user.getType()) {
			case SUPER_ADMIN:
				permissionList.forEach(new Consumer<Permission>() {
					@Override
					public void accept(Permission permission) {
						permissions.add(permission.getName());
					}
				});
				permissions.add(UserType.SUPER_ADMIN.getAuthority());
				permissions.add(UserType.SYSTEM_ADMIN.getAuthority());
				break;
			case SYSTEM_ADMIN:
				permissionRepository
						.findByTypeInAndDeletedFalse(Arrays.asList(UserType.SYSTEM_ADMIN, UserType.USER))
						.forEach(new Consumer<Permission>() {
							@Override
							public void accept(Permission permission) {
								permissions.add(permission.getName());
							}
						});
				permissions.add(UserType.SYSTEM_ADMIN.getAuthority());
				break;
			case USER:
				permissionRepository.findByTypeInAndDeletedFalse(Arrays.asList(UserType.USER))
						.forEach(permission -> permissions.add(permission.getName()));
				break;

		}

		return new CurrentUserDetail(user.getId(), user.getUserName().toLowerCase(), user.getPassword(),
				AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", permissions)), user.getFullName(),
				user.getThumbnail(), user.getEmail(), user.getType(), user.getChatId());

	}
}
