package com.edumento.auth.configuration;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
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
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

/** Created by ahmad on 12/1/16. */
@Configuration
@EnableWebSecurity
public class JWTAuthConfig {

	@Value("${mint.security.signingKey}")
	private String signingKey;

	public static final String AUTHORITIES_KEY = "auth";

	private final UserDetailsService userDetailsService;

	@Autowired
	public JWTAuthConfig(MintProperties mintProperties, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		this.userDetailsService = userDetailsService;

	}

	@Bean
	public SecurityFilterChain standardSecurityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder,
			MvcRequestMatcher.Builder mvc) throws Exception {
		http.cors(Customizer.withDefaults()).csrf(new Customizer<CsrfConfigurer<HttpSecurity>>() {
			@Override
			public void customize(CsrfConfigurer<HttpSecurity> csrf) {
				csrf.disable();
			}
		})
				// .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
				.headers(new Customizer<HeadersConfigurer<HttpSecurity>>() {
					@Override
					public void customize(HeadersConfigurer<HttpSecurity> headers) {
						headers.contentSecurityPolicy(new Customizer<HeadersConfigurer<HttpSecurity>.ContentSecurityPolicyConfig>() {
							@Override
							public void customize(HeadersConfigurer<HttpSecurity>.ContentSecurityPolicyConfig csp) {
								csp.policyDirectives(
										"default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' https://fonts.googleapis.com 'unsafe-inline'; img-src 'self' data:; font-src 'self' https://fonts.gstatic.com data:");
							}
						})
								.frameOptions(FrameOptionsConfig::sameOrigin)
								.referrerPolicy(new Customizer<HeadersConfigurer<HttpSecurity>.ReferrerPolicyConfig>() {
									@Override
									public void customize(HeadersConfigurer<HttpSecurity>.ReferrerPolicyConfig referrer) {
										referrer
												.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN);
									}
								})
								.permissionsPolicy(new Customizer<HeadersConfigurer<HttpSecurity>.PermissionsPolicyConfig>() {
									@Override
									public void customize(
											HeadersConfigurer<HttpSecurity>.PermissionsPolicyConfig permissions) {
										permissions.policy(
												"camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()");
									}
								});
					}
				}).authorizeHttpRequests(new Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry>() {
					@Override
					public void customize(
							AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authz) {
						// prettier-ignore
						authz.requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"),
								mvc.pattern("/*.json"), mvc.pattern("/*.map"), mvc.pattern("/*.css")).permitAll()
								.requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"),
										mvc.pattern("/*.webapp"))
								.permitAll().requestMatchers(mvc.pattern("/app/**")).permitAll()
								.requestMatchers(mvc.pattern("/i18n/**")).permitAll()
								.requestMatchers(mvc.pattern("/content/**")).permitAll()
								.requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
								.requestMatchers(mvc.pattern(HttpMethod.POST, "/api/authenticate")).permitAll()
								.requestMatchers(mvc.pattern(HttpMethod.GET, "/api/authenticate")).permitAll()
								.requestMatchers(mvc.pattern("/api/register")).permitAll()
								.requestMatchers(mvc.pattern("/api/activate")).permitAll()
								.requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
								.requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
								.requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
								.requestMatchers(mvc.pattern("/api/**")).authenticated()
								.requestMatchers(mvc.pattern("/websocket/**")).authenticated()
								.requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
								.requestMatchers(mvc.pattern("/management/health")).permitAll()
								.requestMatchers(mvc.pattern("/management/health/**")).permitAll()
								.requestMatchers(mvc.pattern("/management/info")).permitAll()
								.requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
								.requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN);
					}
				})
				.sessionManagement(new Customizer<SessionManagementConfigurer<HttpSecurity>>() {
					@Override
					public void customize(SessionManagementConfigurer<HttpSecurity> session) {
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
					}
				})
				.exceptionHandling(
						new Customizer<ExceptionHandlingConfigurer<HttpSecurity>>() {
							@Override
							public void customize(ExceptionHandlingConfigurer<HttpSecurity> exceptions) {
								exceptions.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
										.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
							}
						})
				.oauth2ResourceServer(new Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>>() {
					@Override
					public void customize(OAuth2ResourceServerConfigurer<HttpSecurity> oauth2) {
						oauth2.jwt(Customizer.withDefaults());
					}
				})
				.userDetailsService(userDetailsService);

		return http.build();
	}

	@Bean
	public SecretKey secretKey() {
		var keyBytes = Base64.from(signingKey).decode();
		return new SecretKeySpec(keyBytes, MacAlgorithm.HS512.name());
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey secretKey) {
		return NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS512).build();
	}

	@Bean
	public JwtEncoder jwtEecoder(SecretKey secretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {
		var grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
		grantedAuthoritiesConverter.setAuthorityPrefix("");
		grantedAuthoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_KEY);
		var jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);

		return jwtAuthenticationConverter;
	}

	@Bean
	public BearerTokenResolver bearerTokenResolver() {
		var bearerTokenResolver = new DefaultBearerTokenResolver();
		bearerTokenResolver.setAllowUriQueryParameter(false);
		return bearerTokenResolver;
	}

}
