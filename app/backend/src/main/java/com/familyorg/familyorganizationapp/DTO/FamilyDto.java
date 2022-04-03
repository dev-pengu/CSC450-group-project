package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.familyorg.familyorganizationapp.DTO.builder.FamilyDtoBuilder;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class FamilyDto {
  private Long id;
  private String name;
  private String eventColor;
  private String timezone;
  private String inviteCode;
  private Set<FamilyMemberDto> members;
  private FamilyMemberDto owner;
  private UserDto requestingUser;
  @JsonInclude(Include.NON_NULL)
  private List<Role> availableRoles;

  public FamilyDto(Long id, String name, String eventColor, String timezone, String inviteCode,
      FamilyMemberDto owner, Set<FamilyMemberDto> members, UserDto requestingUser,
      List<Role> availableRoles) {
    super();
    this.id = id;
    this.name = name;
    this.eventColor = eventColor;
    this.timezone = timezone;
    this.inviteCode = inviteCode;
    this.members = members;
    this.owner = owner;
    this.requestingUser = requestingUser;
    this.availableRoles = availableRoles;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEventColor() {
    return eventColor;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public Set<FamilyMemberDto> getMembers() {
    return members;
  }

  public FamilyMemberDto getOwner() {
    return owner;
  }

  public String getTimezone() {
    return timezone;
  }

  public UserDto getRequestingUser() {
    return requestingUser;
  }

  public List<Role> getAvailableRoles() {
    return availableRoles;
  }

  public static FamilyDto fromFamilyObj(Family family, User requestingUser) {
    return new FamilyDtoBuilder()
        .withId(family.getId())
        .withEventColor(family.getEventColor())
        .withMembers(
            family.getMembers()
                .stream()
                .map(familyMember -> {
                  FamilyMemberDto memberDto = FamilyMemberDto.fromFamilyMemberObj(familyMember);
                  return memberDto;
                })
                .collect(Collectors.toSet()))
        .withName(family.getName())
        .withTimezone(family.getTimezone())
        .withRequestingUser(UserDto.fromUserObj(requestingUser))
        .withInviteCode(family.getInviteCodeObj().getInviteCodeString())
        .build();
  }

  @Override
  public String toString() {
    return "FamilyDto [id=" + id + ", name=" + name + ", eventColor=" + eventColor + ", timezone="
        + timezone
        + ", inviteCode=" + inviteCode + ", members=" + members + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventColor, id, inviteCode, members, name, timezone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    FamilyDto other = (FamilyDto) obj;
    return Objects.equals(eventColor, other.eventColor) && Objects.equals(id, other.id)
        && Objects.equals(inviteCode, other.inviteCode) && Objects.equals(members, other.members)
        && Objects.equals(name, other.name) && Objects.equals(timezone, other.timezone)
        && Objects.equals(requestingUser, other.requestingUser);
  }


}
