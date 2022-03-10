package com.familyorg.familyorganizationapp.domain.id;

import java.io.Serializable;
import java.util.Objects;

public class MemberInviteId implements Serializable {
  long family;
  String userEmail;
  String inviteCode;

  public MemberInviteId() {}

  public MemberInviteId(Long familyId, String userEmail, String inviteCode) {
    this.family = familyId;
    this.userEmail = userEmail;
    this.inviteCode = inviteCode;
  }

  public long getFamily() {
    return family;
  }

  public void setFamily(long family) {
    this.family = family;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getInviteCode() {
    return inviteCode;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemberInviteId that = (MemberInviteId) o;
    return family == that.family && userEmail.equals(that.userEmail) && inviteCode.equals(that.inviteCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(family, userEmail, inviteCode);
  }
}
