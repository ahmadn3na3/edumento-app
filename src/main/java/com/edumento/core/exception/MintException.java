package com.edumento.core.exception;

import com.edumento.core.constants.Code;

/** Created by ahmad on 10/10/16. */
public class MintException extends RuntimeException {
	private Code code = Code.UNKNOWN;
	private String errorMessage;

	public MintException() {
	}

	public MintException(Code code) {
		this.code = code;
	}

	public MintException(Code code, String errorMessage) {
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public MintException(String s, Code code, String errorMessage) {
		super(errorMessage);
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public MintException(String s, Throwable throwable, Code code, String errorMessage) {
		super(errorMessage, throwable);
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public MintException(Throwable throwable, Code code, String errorMessage) {
		super(throwable);
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public MintException(String s, Throwable throwable, boolean b, boolean b1, Code code, String errorMessage) {
		super(errorMessage, throwable, b, b1);
		this.code = code;
		this.errorMessage = errorMessage;
	}

	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
