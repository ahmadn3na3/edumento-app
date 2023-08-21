package com.edumento.core.controller.abstractcontroller;

import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 3/2/16. */
public abstract class AbstractController<T, ID> {

	public ResponseModel create(T t) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public ResponseModel update(ID id, T t) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public ResponseModel delete(ID id) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public ResponseModel get(Integer page, Integer size) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public ResponseModel get() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	public ResponseModel get(ID id) {
		throw new UnsupportedOperationException("Not Implemented");
	}
}
