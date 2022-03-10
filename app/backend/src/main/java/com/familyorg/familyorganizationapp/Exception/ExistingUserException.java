package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ExistingUserException extends RuntimeException {

	private static final long serialVersionUID = -1570394825629179223L;

	public ExistingUserException() {
		super();
	}

	public ExistingUserException(String message) {
		super(message);
	}

	public ExistingUserException(String message, Throwable cause) {
		super(message, cause);
	}
}
