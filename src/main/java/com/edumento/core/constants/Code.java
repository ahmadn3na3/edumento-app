package com.edumento.core.constants;

/** Created by ahmad on 2/17/16. */
public enum Code {
	SUCCESS(10, ""), MISSING(11, "error.missing"), EXIST(12, "error.exists"), INVALID(13, "error.invalid"),
	INVALID_EMAIL(13, "error.email.invalid"), INVALID_PASSWORD(13, "error.password.invalid"),
	INVALID_KEY(13, "error.key.invalid"), INVALID_CODE(13, "error.code.invalid"), UNKNOWN(14, "error.unknown"),
	NOT_FOUND(15, "error.not.found"), UPLOAD_IMAGE(16, "error.upload.image"), UPLOAD_DATA(16, "error.upload.data"),
	UPLOAD_THUMB(16, "error.upload.thumb"), UPLOAD_EXCEED(16, "error.upload.exceed"),
	ORGANIZATION_LIMIT(17, "error.organization.limit"), ORGANIZATION_ACTIVE(17, "error.organization.active"),
	NOT_ACTIVATED(19, "error.account.not.activated"), NOT_PERMITTED(21, "error.forbidden"),
	NOT_READY(20, "error.content.notready"),

	// public static final String DESCRIPTION = "error_description";
	// public static final String URI = "error_uri";
	// public static final String INVALID_REQUEST = "invalid_request";
	// public static final String INVALID_CLIENT = "invalid_client";
	// public static final String INVALID_GRANT = "invalid_grant";
	// public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
	// public static final String UNSUPPORTED_GRANT_TYPE =
	// "unsupported_grant_type";
	// public static final String INVALID_SCOPE = "invalid_scope";
	// public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
	INVALID_TOKEN(33, ""), NOT_ENOUGH(35, "error.notenoughdata");
	// public static final String REDIRECT_URI_MISMATCH =
	// "redirect_uri_mismatch";
	// public static final String UNSUPPORTED_RESPONSE_TYPE =
	// "unsupported_response_type";
	// public static final String ACCESS_DENIED = "access_denied"

	private int code;
	private String message;

	Code(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public static Code getByIntCode(int code) {
		for (Code codes : Code.values()) {
			if (code == codes.getCode()) {
				return codes;
			}
		}
		return null;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
