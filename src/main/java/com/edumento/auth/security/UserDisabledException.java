package com.edumento.auth.security;

import com.edumento.core.constants.Code;
import com.edumento.core.exception.MintException;

/**
 * This exception is throw in case of a not activated user trying to
 * authenticate.
 */
public class UserDisabledException extends MintException {

	public UserDisabledException(String message) {
		super(Code.NOT_ACTIVATED, message);
	}

	public UserDisabledException(String message, Throwable t) {
		super(t, Code.NOT_ACTIVATED, message);
	}

	public String getOAuth2ErrorCode() {
		return "user_disabled";
	}
}
