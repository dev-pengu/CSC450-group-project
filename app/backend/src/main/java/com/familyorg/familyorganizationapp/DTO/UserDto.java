package com.familyorg.familyorganizationapp.DTO;

import java.util.Objects;

import com.familyorg.familyorganizationapp.domain.User;

public class UserDto {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String username;
	public UserDto(Long id, String firstName, String lastName, String email, String username) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.username = username;
	}
	public Long getId() {
		return id;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public String getEmail() {
		return email;
	}
	public String getUsername() {
		return username;
	}
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", username=" + username + "]";
	}
	@Override
	public int hashCode() {
		return Objects.hash(email, firstName, id, lastName, username);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		return Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(username, other.username);
	}

	public static UserDto fromUserObj(User user) {
		return new UserDto(
				user.getId(),
				user.getFirstName(),
				user.getLastName(),
				user.getEmail(),
				user.getUsername());
	}

}
