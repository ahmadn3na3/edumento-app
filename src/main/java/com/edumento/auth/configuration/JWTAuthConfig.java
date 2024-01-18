package com.edumento.auth.configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import com.edumento.core.configuration.MintProperties;
import com.edumento.core.constants.AuthoritiesConstants;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;

/** Created by ahmad on 12/1/16. */
@Configuration
@EnableWebSecurity
public class JWTAuthConfig {

	private final MintProperties mintProperties;
	@Value("${mint.security.signingKey}")
	private String signingKey;

	public static final String AUTHORITIES_KEY = "auth";

	private final UserDetailsService userDetailsService;

	@Autowired
	public JWTAuthConfig(MintProperties mintProperties,
			UserDetailsService userDetailsService) {
		this.mintProperties = mintProperties;

		this.userDetailsService = userDetailsService;

	}
	@Bean
	public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http,
			JWKSource<com.nimbusds.jose.proc.SecurityContext> jwkSource,
			MvcRequestMatcher.Builder mvc) throws Exception {
		http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				// .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
				.headers(headers -> {
					headers.contentSecurityPolicy(csp -> csp.policyDirectives(
							"default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; img-src 'self' data:; font-src 'self' https://fonts.gstatic.com data:"))
							.frameOptions(FrameOptionsConfig::sameOrigin)
							.referrerPolicy(referrer -> referrer.policy(
									ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
							.permissionsPolicy(permissions -> permissions.policy(
									"camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"));
				}).authorizeHttpRequests(authz ->
				// prettier-ignore
				authz.requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"),
						mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"),
						mvc.pattern("/*.css")).permitAll()
						.requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"),
								mvc.pattern("/*.svg"), mvc.pattern("/*.webapp"))
						.permitAll().requestMatchers(mvc.pattern("/app/**")).permitAll()
						.requestMatchers(mvc.pattern("/i18n/**")).permitAll()
						.requestMatchers(mvc.pattern("/content/**")).permitAll()
						.requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
						.requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate"))
						.permitAll()
						.requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate"))
						.permitAll().requestMatchers(mvc.pattern("/api/register")).permitAll()
						.requestMatchers(mvc.pattern("/api/activate")).permitAll()
						.requestMatchers(mvc.pattern("/api/account/reset-password/init"))
						.permitAll()
						.requestMatchers(mvc.pattern("/api/account/reset-password/finish"))
						.permitAll().requestMatchers(mvc.pattern("/api/admin/**"))
						.hasAuthority(AuthoritiesConstants.ADMIN)
						.requestMatchers(mvc.pattern("/api/**")).authenticated()
						.requestMatchers(mvc.pattern("/websocket/**")).authenticated()
						.requestMatchers(mvc.pattern("/v3/api-docs/**"))
						.hasAuthority(AuthoritiesConstants.ADMIN)
						.requestMatchers(mvc.pattern("/management/health")).permitAll()
						.requestMatchers(mvc.pattern("/management/health/**")).permitAll()
						.requestMatchers(mvc.pattern("/management/info")).permitAll()
						.requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
						.requestMatchers(mvc.pattern("/management/**"))
						.hasAuthority(AuthoritiesConstants.ADMIN))
				.sessionManagement(
						session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
						.accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
				.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
				.userDetailsService(userDetailsService);

		return http.build();
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

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
				new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("");
		grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY);

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
		return jwtAuthenticationConverter;
	}

	@Bean
	public BearerTokenResolver bearerTokenResolver() {
		var bearerTokenResolver = new DefaultBearerTokenResolver();
		bearerTokenResolver.setAllowUriQueryParameter(true);
		return bearerTokenResolver;
	}

}
