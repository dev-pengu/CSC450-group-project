package com.familyorg.familyorganizationapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name="first_name", columnDefinition="VARCHAR(50)", nullable=false)
	private String firstName;
	
	@Column(name="last_name", columnDefinition="VARCHAR(50)", nullable=false)
	private String lastName;
	
	@Column(name="username", columnDefinition="VARCHAR(50)", nullable=false, unique=true)
	private String username;
	
	@Column(name="password", columnDefinition="VARCHAR(64)", nullable=false)
	private String password;
	
	@Column(name="email", columnDefinition="VARCHAR(70)", nullable=false, unique=true)
	private String email;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", username=" + username
				+ ", email=" + email + "]";
	}
	
	
}
