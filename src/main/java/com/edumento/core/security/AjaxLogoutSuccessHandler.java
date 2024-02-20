package com.edumento.core.security;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.edumento.core.model.ResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Spring Security logout handler, specialized for Ajax requests. */
@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
		implements LogoutSuccessHandler {

	public static final String BEARER_AUTHENTICATION = "Bearer ";
	private static final String HEADER_AUTHORIZATION = "Authorization";

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		// Request the token
		var token = request.getHeader(HEADER_AUTHORIZATION);

		if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
			token.split(" ");

			// OAuth2AccessToken oAuth2AccessToken =
			// tokenStore.readAccessToken(accessTokenValue);
			// if (oAuth2AccessToken != null) {
			// OAuth2RefreshToken oAuth2RefreshToken = oAuth2AccessToken.getRefreshToken();
			// if (oAuth2RefreshToken != null)
			// tokenStore.removeRefreshToken(oAuth2RefreshToken);

			// tokenStore.removeAccessToken(oAuth2AccessToken);
			// }
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), ResponseModel.done());
	}
}
