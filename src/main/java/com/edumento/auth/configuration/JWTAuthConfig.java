package com.edumento.auth.configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.edumento.core.configuration.MintProperties;
import com.edumento.core.constants.Services;
import com.edumento.user.domain.Permission;
import com.edumento.user.repo.PermissionRepository;
import com.edumento.user.repo.UserRepository;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

/** Created by ahmad on 12/1/16. */
@Configuration
@EnableWebSecurity
public class JWTAuthConfig {

	private final AuthenticationManager authenticationManager;
	private final MintProperties mintProperties;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${mint.security.signingKey}")
	private String signingKey;

	private final PermissionRepository permissionRepository;
	private final Set<String> questionBankPermision;

	@Autowired
	public JWTAuthConfig(AuthenticationManager authenticationManager, MintProperties mintProperties,
			UserRepository userRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.mintProperties = mintProperties;
		this.userRepository = userRepository;
		this.permissionRepository = permissionRepository;
		questionBankPermision = new HashSet<>(Arrays.asList("QUESTION_REVIEW_READ", "QUESTION_REVIEW_UPDATE",
				"QUESTION_REVIEW_DELETE", "QUESTION_CREATE", "QUESTION_UPDATE", "QUESTION_DELETE"));
		this.passwordEncoder = passwordEncoder;

	}

	// @Bean
	// public TokenStore tokenStore() {
	// JdbcTokenStore tokenStore = new JdbcTokenStore(dataSource);
	// return tokenStore;
	// }

	@Bean
	public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http,
			JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
		http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
				.formLogin(Customizer.withDefaults()).httpBasic((httpbasic) -> {
					httpbasic.disable();
					httpbasic.realmName("edumento");

				}).sessionManagement((sessionManagment) -> {
					sessionManagment.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

				}).authorizeHttpRequests((authorizeRequsts) -> {
					authorizeRequsts
							.requestMatchers("/oauth/authorize", "/oauth/token", "check_token",
									"/scripts/**/*.{js,html}", "/bower_components/**", "/i18n/**", "/assets/**",
									"/api/register", "/api/activate/**", "/api/reactivate", "/api/time",
									"/api/account/forget_password/init", "/api/account/forget_password/finish",
									"/api/account/forget_password/checkcode/**")
							.permitAll();
					authorizeRequsts.requestMatchers("/api/**").authenticated();
				});

		return http.build();

	}

	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		Set<String> permissions = permissionRepository.findAll().stream().map(Permission::getName)
				.collect(Collectors.toSet());

		// RegisteredClient androidClient =
		// RegisteredClient.withId(UUID.randomUUID().toString())
		// .clientId(mintProperties.getSecurity().getAuthentication().getOauthAndroid()
		// .getClientid())
		// .scope("write")
		// .scope("read")
		// .scope(OidcScopes.OPENID)
		// .scope(OidcScopes.PROFILE)
		// .scopes(scopes -> scopes.addAll(permissions))
		// .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
		// .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		// .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
		// .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)

		// .clientSecret(mintProperties.getSecurity().getAuthentication().getOauthAndroid()
		// .getSecret())
		// .build();
		RegisteredClient webClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId(mintProperties.getSecurity().getAuthentication().getOauthWeb().getClientid()).scope("write")
				.scope("read").scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE)
				.scopes(scopes -> scopes.addAll(permissions)).authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.clientSecret(mintProperties.getSecurity().getAuthentication().getOauthWeb().getSecret()).build();

		// RegisteredClient cpClient =
		// RegisteredClient.withId(UUID.randomUUID().toString())
		// .clientId(
		// mintProperties.getSecurity().getAuthentication().getOauthControlPanel()
		// .getClientid())
		// .scope("write")
		// .scope("read")
		// .scope(OidcScopes.OPENID)
		// .scope(OidcScopes.PROFILE)
		// .scopes(scopes -> scopes.addAll(permissions))
		// .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
		// .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		// .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
		// .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
		// .clientSecret(mintProperties.getSecurity().getAuthentication().getOauthControlPanel()
		// .getSecret())
		// .build();
		// RegisteredClient qbClient =
		// RegisteredClient.withId(UUID.randomUUID().toString())
		// .clientId(
		// mintProperties.getSecurity().getAuthentication().getOauthQuestionBank()
		// .getClientid())
		// .scope("write")
		// .scope("read")
		// .scope(OidcScopes.OPENID)
		// .scope(OidcScopes.PROFILE)
		// .scopes(scopes -> scopes.addAll(permissions))
		// .authorizationGrantType(AuthorizationGrantType.JWT_BEARER)
		// .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
		// .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
		// .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
		// .clientSecret(mintProperties.getSecurity().getAuthentication().getOauthQuestionBank()
		// .getSecret())
		// .build();

		return new InMemoryRegisteredClientRepository(webClient);
	}

	@Bean
	public JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource(KeyPair keyPair) {
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
		// @formatter:off
		RSAKey rsaKey = new RSAKey.Builder(publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		// @formatter:on
		JWKSet jwkSet = new JWKSet(rsaKey);
		return new ImmutableJWKSet<>(jwkSet);
	}

	@Bean
	public JwtDecoder jwtDecoder(KeyPair keyPair) {
		return NimbusJwtDecoder.withPublicKey((RSAPublicKey) keyPair.getPublic()).build();
	}

	@Bean
	public AuthorizationServerSettings providerSettings() {
		return AuthorizationServerSettings.builder().issuer("http://localhost:8080").build();
	}

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	KeyPair generateRsaKey() {
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

}
