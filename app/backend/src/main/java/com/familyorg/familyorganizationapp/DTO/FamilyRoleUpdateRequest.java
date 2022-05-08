package com.familyorg.familyorganizationapp.DTO;

import java.util.List;

public class FamilyRoleUpdateRequest {
  private Long familyId;
  private List<FamilyMemberDto> members;

  public Long getFamilyId() {
    return familyId;
  }

  public void setFamilyId(Long familyId) {
    this.familyId = familyId;
  }

  public List<FamilyMemberDto> getMembers() {
    return members;
  }

  public void setMembers(List<FamilyMemberDto> members) {
    this.members = members;
  }

  @Override
  public String toString() {
    return "FamilyRoleUpdateRequest [familyId=" + familyId + ", members=" + members + "]";
  }
}
