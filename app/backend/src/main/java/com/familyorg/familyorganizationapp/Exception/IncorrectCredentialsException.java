package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class IncorrectCredentialsException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -2850539484606865023L;

	public IncorrectCredentialsException() {
		super();
	}

	public IncorrectCredentialsException(String message) {
		super(message);
	}

	public IncorrectCredentialsException(String message, Throwable cause) {
		super(message, cause);
	}
}
