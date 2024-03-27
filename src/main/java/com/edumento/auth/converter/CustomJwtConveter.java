package com.edumento.auth.converter;

import java.util.Collection;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.edumento.core.security.CurrentUserDetail;
import com.edumento.user.constant.UserType;

public class CustomJwtConveter implements Converter<Jwt, AbstractAuthenticationToken> {
	private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

	public CustomJwtConveter() {
		super();
	}

	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
		CurrentUserDetail currentUserDetail = new CurrentUserDetail(jwt.getClaim("id"), jwt.getSubject(), "",
				getJwtGrantedAuthoritiesConverter().convert(jwt), jwt.getClaim("fullName"), jwt.getClaim("image"),
				jwt.getClaim("email"), jwt.getClaim("foundationid"), jwt.getClaim("companyId"),
				UserType.valueOf(jwt.getClaimAsString("type")), "chatId");
		return new PreAuthenticatedAuthenticationToken(currentUserDetail, null,
				getJwtGrantedAuthoritiesConverter().convert(jwt));
		//

	}

	public Converter<Jwt, Collection<GrantedAuthority>> getJwtGrantedAuthoritiesConverter() {
		return jwtGrantedAuthoritiesConverter;
	}

	public void setJwtGrantedAuthoritiesConverter(
			Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
		this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
	}
}
