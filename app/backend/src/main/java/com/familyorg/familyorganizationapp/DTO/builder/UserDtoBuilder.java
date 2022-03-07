package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.UserDto;

public class UserDtoBuilder {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	
	public UserDtoBuilder withId(Long id) {
		this.id = id;
		return this;
	}
	
	public UserDtoBuilder withFirstName(String firstName) {
		this.firstName = firstName;
		return this;
	}
	
	public UserDtoBuilder withLastName(String lastName) {
		this.lastName = lastName;
		return this;
	}
	
	public UserDtoBuilder withEmail(String email) {
		this.email = email;
		return this;
	}
	
	public UserDtoBuilder withUsername(String username) {
		this.username = username;
		return this;
	}
	
	public UserDto build() {
		return new UserDto(
				this.id,
				this.firstName, 
				this.lastName, 
				this.email, 
				this.username);
	}
}
