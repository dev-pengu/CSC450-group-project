package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ColorDto {
  @JsonInclude(Include.NON_NULL)
  private final Long familyId;

  @JsonInclude(Include.NON_NULL)
  private final Long userId;

  @JsonInclude(Include.NON_NULL)
  private final String user;

  @JsonInclude(Include.NON_NULL)
  private final String family;

  @JsonInclude(Include.NON_NULL)
  private final String color;

  public ColorDto(Long familyId, Long userId, String user, String family, String color) {
    super();
    this.familyId = familyId;
    this.userId = userId;
    this.user = user == null ? null : user.trim();
    this.family = family == null ? null : family.trim();
    this.color = color == null ? null : color.trim();
  }

  public Long getFamilyId() {
    return familyId;
  }

  public Long getUserId() {
    return userId;
  }

  public String getUser() {
    return user;
  }

  public String getFamily() {
    return family;
  }

  public String getColor() {
    return color;
  }

  @Override
  public String toString() {
    return "ColorDto [familyId="
        + familyId
        + ", userId="
        + userId
        + ", user="
        + user
        + ", family="
        + family
        + ", color="
        + color
        + "]";
  }
}
