package com.familyorg.familyorganizationapp.DTO;


import com.familyorg.familyorganizationapp.domain.Role;

import java.util.Objects;

public class MemberInviteDto {
  private Long familyId;
  private String recipientEmail;
  private Role initialRole;
  private boolean persistent;

  public MemberInviteDto(Long familyId, String recipientEmail, Role initialRole, boolean persistent) {
    this.familyId = familyId;
    this.recipientEmail = recipientEmail;
    this.initialRole = initialRole;
    this.persistent = persistent;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public String getRecipientEmail() {
    return recipientEmail;
  }

  public Role getInitialRole() {
    return initialRole;
  }

  public boolean isPersistent() { return persistent; }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemberInviteDto that = (MemberInviteDto) o;
    return persistent == that.persistent &&
        familyId.equals(that.familyId) &&
        Objects.equals(recipientEmail, that.recipientEmail) &&
        initialRole == that.initialRole;
  }

  @Override
  public int hashCode() {
    return Objects.hash(familyId, recipientEmail, initialRole, persistent);
  }

  @Override
  public String toString() {
    return "MemberInviteDto{" +
        "familyId=" + familyId +
        ", recipientEmail='" + recipientEmail + '\'' +
        ", initialRole=" + initialRole +
        ", isPersistent=" + persistent +
        '}';
  }
}
