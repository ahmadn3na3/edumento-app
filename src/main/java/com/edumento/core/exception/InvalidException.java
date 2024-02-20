package com.edumento.core.exception;

import com.edumento.core.constants.Code;

/** Created by ahmad on 4/18/17. */
public class InvalidException extends MintException {
	public InvalidException() {
		super(Code.INVALID);
	}

	public InvalidException(String errorMessage) {
		super(Code.INVALID, errorMessage);
	}

	public InvalidException(String s, String errorMessage) {
		super(s, Code.INVALID, errorMessage);
	}

	public InvalidException(String s, Throwable throwable, String errorMessage) {
		super(s, throwable, Code.INVALID, errorMessage);
	}

	public InvalidException(Throwable throwable, String errorMessage) {
		super(throwable, Code.INVALID, errorMessage);
	}

	public InvalidException(String s, Throwable throwable, boolean b, boolean b1, String errorMessage) {
		super(s, throwable, b, b1, Code.INVALID, errorMessage);
	}
}
