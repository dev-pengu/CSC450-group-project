package com.familyorg.familyorganizationapp.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FamilyNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7181315385449476949L;

	public FamilyNotFoundException() {
		super();
	}

	public FamilyNotFoundException(String message) {
		super(message);
	}

	public FamilyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
