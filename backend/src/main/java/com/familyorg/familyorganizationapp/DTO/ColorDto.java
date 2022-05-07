package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ColorDto {
  @JsonInclude(Include.NON_NULL)
  private Long familyId;
  @JsonInclude(Include.NON_NULL)
  private Long userId;
  @JsonInclude(Include.NON_NULL)
  private String user;
  @JsonInclude(Include.NON_NULL)
  private String family;
  @JsonInclude(Include.NON_NULL)
  private String color;

  public ColorDto(Long familyId, Long userId, String user, String family, String color) {
    super();
    this.familyId = familyId;
    this.userId = userId;
    this.user = user;
    this.family = family;
    this.color = color;
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
    return "ColorDto [familyId=" + familyId + ", userId=" + userId + ", user=" + user + ", family="
        + family + ", color=" + color + "]";
  }


}
