package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.List;
import com.familyorg.familyorganizationapp.DTO.ColorDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;

public class UserDtoBuilder implements DtoBuilder<UserDto> {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private String timezone;
  private Boolean darkMode;
  private List<ColorDto> colorsByFamily;

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

  public UserDtoBuilder withTimezone(String timezone) {
    this.timezone = timezone;
    return this;
  }

  public UserDtoBuilder setDarkMode(Boolean useDarkMode) {
    this.darkMode = useDarkMode;
    return this;
  }

  public UserDtoBuilder withColorsByFamily(List<ColorDto> colors) {
    this.colorsByFamily = colors;
    return this;
  }

  public UserDtoBuilder fromUserObj(User user) {
    this.username = user.getUsername();
    this.id = user.getId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.darkMode = user.useDarkMode();
    this.timezone = user.getTimezone();
    return this;
  }

  @Override
  public UserDto build() {
    return new UserDto(this.id, this.firstName, this.lastName, this.email, this.username, null,
        null, timezone, darkMode, this.colorsByFamily);
  }
}
