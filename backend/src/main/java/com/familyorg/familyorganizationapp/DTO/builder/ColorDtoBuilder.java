package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ColorDto;

public class ColorDtoBuilder implements DtoBuilder<ColorDto> {
  private Long familyId;
  private Long userId;
  private String user;
  private String family;
  private String color;

  public ColorDtoBuilder withFamilyId(Long id) {
    familyId = id;
    return this;
  }

  public ColorDtoBuilder withUserId(Long id) {
    userId = id;
    return this;
  }

  public ColorDtoBuilder withUser(String user) {
    this.user = user;
    return this;
  }

  public ColorDtoBuilder withFamily(String family) {
    this.family = family;
    return this;
  }

  public ColorDtoBuilder withColor(String color) {
    this.color = color;
    return this;
  }

  @Override
  public ColorDto build() {
    return new ColorDto(familyId, userId, user, family, color);
  }
}
