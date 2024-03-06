package com.edumento.user.controller;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.auth.configuration.JWTAuthConfig;
import com.edumento.core.security.CurrentUserDetail;
import com.edumento.user.model.account.LoginModel;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class AuthenticateController {

	private final Logger log = LoggerFactory.getLogger(AuthenticateController.class);

	private final JwtEncoder jwtEncoder;

	@Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:18600}")
	private long tokenValidityInSeconds;

	@Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
	private long tokenValidityInSecondsForRememberMe;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public AuthenticateController(JwtEncoder jwtEncoder, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.jwtEncoder = jwtEncoder;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}

	@PostMapping("/authenticate")
	public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginModel loginVM) {
		var authenticationToken = new UsernamePasswordAuthenticationToken(loginVM.username(), loginVM.password());

		var authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		var jwt = createToken(authentication, loginVM.rememberMe());
		var httpHeaders = new HttpHeaders();
		httpHeaders.setBearerAuth(jwt);
		return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
	}

	/**
	 * {@code GET /authenticate} : check if the user is authenticated, and return
	 * its login.
	 *
	 * @param request the HTTP request.
	 * @return the login if the user is authenticated.
	 */
	@GetMapping("/authenticate")
	public String isAuthenticated(HttpServletRequest request) {
		log.debug("REST request to check if the current user is authenticated");
		return request.getRemoteUser();
	}

	public String createToken(Authentication authentication, boolean rememberMe) {
		var authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(" "));

		var now = Instant.now();
		Instant validity;
		if (rememberMe) {
			validity = now.plus(tokenValidityInSecondsForRememberMe, ChronoUnit.SECONDS);
		} else {
			validity = now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS);
		}
		var userdetails = (CurrentUserDetail) authentication.getPrincipal();
		// @formatter:off
        var claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(JWTAuthConfig.AUTHORITIES_KEY, authorities)
            .claim("id", userdetails.getId())
            .claim("companyId",  Optional.ofNullable(userdetails.getOrganizationId()).orElse(-1L))
            .claim("foundationid", Optional.ofNullable(userdetails.getFoundationId()).orElse(-1L))
            .claim("type", userdetails.getType())
            .claim("email", userdetails.getEmail())
            .claim("image", Optional.ofNullable( userdetails.getImage()).orElse(""))
            .claim("fullName", userdetails.getFullName())
            .claim("accountNonExpired", userdetails.isAccountNonExpired())
            .claim("accountNonLocked", userdetails.isAccountNonLocked())
            .claim("credentialsNonExpired", userdetails.isCredentialsNonExpired())
            .claim("enabled", userdetails.isEnabled())
            .build();

        var jwsHeader = JwsHeader.with(MacAlgorithm.HS512).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
