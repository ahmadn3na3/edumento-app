package com.edumento.auth.security;

import com.edumento.core.constants.Code;
import com.edumento.core.exception.MintException;

/**
 * This exception is throw in case of a not activated user trying to
 * authenticate.
 */
public class UserExpiredException extends MintException {

	public UserExpiredException(String message) {
		super(Code.INVALID, message);
	}

	public UserExpiredException(String message, Throwable t) {
		super(t, Code.INVALID, message);
	}

	public String getOAuth2ErrorCode() {
		return "user_expired";
	}
}
