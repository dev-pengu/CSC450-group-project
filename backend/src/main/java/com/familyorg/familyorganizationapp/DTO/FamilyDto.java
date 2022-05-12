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
  private final Long id;
  private final String name;
  private final String eventColor;
  private final String timezone;
  private final String inviteCode;
  private final Set<FamilyMemberDto> members;
  private final FamilyMemberDto owner;
  private final UserDto requestingUser;

  @JsonInclude(Include.NON_NULL)
  private final List<Role> availableRoles;

  private final FamilyMemberDto memberData;

  public FamilyDto(
      Long id,
      String name,
      String eventColor,
      String timezone,
      String inviteCode,
      FamilyMemberDto owner,
      Set<FamilyMemberDto> members,
      UserDto requestingUser,
      List<Role> availableRoles,
      FamilyMemberDto memberData) {
    super();
    this.id = id;
    this.name = name == null ? null : name.trim();
    this.eventColor = eventColor == null ? null : eventColor.trim();
    this.timezone = timezone == null ? null : timezone.trim();
    this.inviteCode = inviteCode == null ? null : inviteCode.trim();
    this.members = members;
    this.owner = owner;
    this.requestingUser = requestingUser;
    this.availableRoles = availableRoles;
    this.memberData = memberData;
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

  public FamilyMemberDto getMemberData() {
    return memberData;
  }

  public static FamilyDto fromFamilyObj(Family family, User requestingUser) {
    return new FamilyDtoBuilder()
        .withId(family.getId())
        .withEventColor(family.getEventColor())
        .withMembers(
            family.getMembers().stream()
                .map(
                    familyMember -> {
                      FamilyMemberDto memberDto = FamilyMemberDto.fromFamilyMemberObj(familyMember);
                      return memberDto;
                    })
                .collect(Collectors.toSet()))
        .withName(family.getName())
        .withTimezone(family.getTimezone())
        .withInviteCode(family.getInviteCodeObj().getInviteCodeString())
        .withMemberData(
            FamilyMemberDto.fromFamilyMemberObj(
                family.getMembers().stream()
                    .filter(member -> member.getUser().getId().equals(requestingUser.getId()))
                    .findFirst()
                    .get()))
        .build();
  }

  @Override
  public String toString() {
    return "FamilyDto [id="
        + id
        + ", name="
        + name
        + ", eventColor="
        + eventColor
        + ", timezone="
        + timezone
        + ", inviteCode="
        + inviteCode
        + ", members="
        + members
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventColor, id, inviteCode, members, name, timezone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    FamilyDto other = (FamilyDto) obj;
    return Objects.equals(eventColor, other.eventColor)
        && Objects.equals(id, other.id)
        && Objects.equals(inviteCode, other.inviteCode)
        && Objects.equals(members, other.members)
        && Objects.equals(name, other.name)
        && Objects.equals(timezone, other.timezone)
        && Objects.equals(requestingUser, other.requestingUser);
  }
}
