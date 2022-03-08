package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User implements Serializable {

	private static final long serialVersionUID = -4364743442691665173L;

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

	@OneToMany(mappedBy="user", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
	private Set<FamilyMembers> families;



	public User() {}


	public User(Long id, String firstName, String lastName, String username, String password, String email,
			Set<FamilyMembers> families) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.email = email;
		this.families = families;
	}

	public User(String firstName, String lastName, String username, String password, String email,
				Set<FamilyMembers> families) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.email = email;
		this.families = families;
	}

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
	public Set<FamilyMembers> getFamilies() {
		return families;
	}
	public void setFamilies(Set<FamilyMembers> families) {
		this.families = families;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", username=" + username
				+ ", email=" + email + "]";
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
		User other = (User) obj;
		return Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(id, other.id)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(password, other.password)
				&& Objects.equals(username, other.username);
	}



}
