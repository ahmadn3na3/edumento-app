package com.edumento.core.exception;

import com.edumento.core.constants.Code;

/** Created by ahmad on 1/30/17. */
public class ExistException extends MintException {
	private Object data;

	public ExistException() {
		super(Code.EXIST);
	}

	public ExistException(Object data) {
		this();
		this.data = data;
	}

	public ExistException(String errorMessage) {
		super(Code.EXIST, errorMessage);
	}

	public ExistException(String s, String errorMessage) {
		super(s, Code.EXIST, errorMessage);
	}

	public ExistException(String s, Throwable throwable, String errorMessage) {
		super(s, throwable, Code.EXIST, errorMessage);
	}

	public ExistException(Throwable throwable, String errorMessage) {
		super(throwable, Code.EXIST, errorMessage);
	}

	public ExistException(String s, Throwable throwable, boolean b, boolean b1, String errorMessage) {
		super(s, throwable, b, b1, Code.EXIST, errorMessage);
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
