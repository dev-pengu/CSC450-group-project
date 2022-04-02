package com.familyorg.familyorganizationapp.DTO;

import java.util.Objects;
import com.familyorg.familyorganizationapp.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UserDto {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  @JsonInclude(Include.NON_NULL)
  private String oldPassword;
  @JsonInclude(Include.NON_NULL)
  private String newPassword;
  private String timezone;

  public UserDto(Long id, String firstName, String lastName, String email, String username,
      String oldPassword, String newPassword, String timezone) {
    super();
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
    this.oldPassword = oldPassword;
    this.newPassword = newPassword;
    this.timezone = timezone;
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

  public String getOldPassword() {
    return oldPassword;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public String getTimezone() {
    return timezone;
  }

  @Override
  public String toString() {
    return "UserDto [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email="
        + email + ", username=" + username + ", timezone=" + timezone + "]";
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
    return new UserDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(),
        user.getUsername(), null, null, user.getTimezone());
  }

}
