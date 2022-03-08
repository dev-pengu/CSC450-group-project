package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.Role;

public interface InviteService {
  /**
   * Generates an invite code given the familyId and userEmail. The role used will be the
   * lowest level role.
   * @param familyId The family invited to
   * @param userEmail The user being invited.
   * @return A generated MemberInvite to be consumed by other services.
   */
  MemberInvite createUniqueMemberInvite(Long familyId, String userEmail) throws FamilyNotFoundException, AuthorizationException;

  /**
   * Generates an invite code given the familyId, userEmail, and role.
   * @param familyId The family invited to
   * @param userEmail The user being invited
   * @param role The role to assign the user when joining the family
   * @return a generated MemberInvite to be consumed by other services.
   */
  MemberInvite createUniqueMemberInviteWithRole(Long familyId, String userEmail, Role role) throws FamilyNotFoundException, AuthorizationException;

  /**
   * Generates an invite code to persist on the family table. Subsequent calls to this
   * method with the same familyId will overwrite the current value stored on the family.
   * @param familyId The family Id to generate a code for
   * @return The string representation of the uuid generated
   */
  FamilyDto generatePersistentMemberInvite(Long familyId) throws FamilyNotFoundException, AuthorizationException;

  /**
   * Verifies if a member invite exists and returns the full MemberInvite with the role
   * to be assigned to the user on successful addition to the family. On successful
   * verification, if a non-persistent code, it will be removed.
   * @param memberInvite a member invite with user's email, family id, and the invite code
   * @param isPersistentCode whether the code passed to this function is the persistent
   *                         invite code stored on the family. If true, a lookup to family
   *                         objects will be performed, if false, member_invite will be queried
   * @return The full memberInvite stored in the database, or null if it doesn't exist
   */
  MemberInvite verifyMemberInvite(MemberInvite memberInvite, boolean isPersistentCode);
}
