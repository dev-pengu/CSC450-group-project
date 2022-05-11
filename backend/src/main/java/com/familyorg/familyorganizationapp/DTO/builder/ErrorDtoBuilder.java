package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ErrorDto;

public class ErrorDtoBuilder implements DtoBuilder<ErrorDto> {
	private String message;
	private int errorCode;
	private boolean redirect = false;
	private String redirectUrl;

	public ErrorDtoBuilder withMessage(String message) {
		this.message = message;
		return this;
	}

	public ErrorDtoBuilder withErrorCode(int errorCode) {
		this.errorCode = errorCode;
		return this;
	}

	public ErrorDtoBuilder addRedirect(String redirectUrl) {
		this.redirectUrl = redirectUrl;
		this.redirect = true;
		return this;
	}

	@Override
	public ErrorDto build() {
		return new ErrorDto(
				message,
				errorCode,
				redirect,
				redirectUrl);
	}
}
