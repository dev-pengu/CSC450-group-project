package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;
import com.familyorg.familyorganizationapp.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class UserDto {
  private final Long id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String username;

  @JsonInclude(Include.NON_NULL)
  private final String oldPassword;

  @JsonInclude(Include.NON_NULL)
  private final String newPassword;

  private final String timezone;

  @JsonInclude(Include.NON_NULL)
  private final Boolean useDarkMode;

  @JsonInclude(Include.NON_NULL)
  private final List<ColorDto> colorsByFamily;

  @JsonInclude(Include.NON_NULL)
  private final String resetCode;

  public UserDto(
      Long id,
      String firstName,
      String lastName,
      String email,
      String username,
      String oldPassword,
      String newPassword,
      String timezone,
      Boolean useDarkMode,
      List<ColorDto> colorsByFamily,
      String resetCode) {
    super();
    this.id = id;
    this.firstName = firstName == null ? null : firstName.trim();
    this.lastName = lastName == null ? null : lastName.trim();
    this.email = email == null ? null : email.trim().toLowerCase();
    this.username = username == null ? null : username.trim().toLowerCase();
    this.oldPassword = oldPassword == null ? null : oldPassword.trim();
    this.newPassword = newPassword == null ? null : newPassword.trim();
    this.timezone = timezone == null ? null : timezone.trim();
    this.useDarkMode = useDarkMode;
    this.colorsByFamily = colorsByFamily;
    this.resetCode = resetCode == null ? null : resetCode.trim();
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

  public Boolean getUseDarkMode() {
    return useDarkMode;
  }

  public List<ColorDto> getColorsByFamily() {
    return colorsByFamily;
  }

  public String getResetCode() {
    return resetCode;
  }

  @Override
  public String toString() {
    return "UserDto [id="
        + id
        + ", firstName="
        + firstName
        + ", lastName="
        + lastName
        + ", email="
        + email
        + ", username="
        + username
        + ", timezone="
        + timezone
        + ", useDarkMode="
        + useDarkMode
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, firstName, id, lastName, username);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    UserDto other = (UserDto) obj;
    return Objects.equals(email, other.email)
        && Objects.equals(firstName, other.firstName)
        && Objects.equals(id, other.id)
        && Objects.equals(lastName, other.lastName)
        && Objects.equals(username, other.username);
  }

  public static UserDto fromUserObj(User user) {
    return new UserDto(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getUsername(),
        null,
        null,
        user.getTimezone(),
        user.useDarkMode(),
        null,
        null);
  }
}
