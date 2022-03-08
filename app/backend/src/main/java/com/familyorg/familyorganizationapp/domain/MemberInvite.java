package com.familyorg.familyorganizationapp.domain;

import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name="member_invite")
@IdClass(MemberInviteId.class)
public class MemberInvite {

  @Id
  @Column(name="invite_code", columnDefinition="VARCHAR(36)", nullable=false)
  private String inviteCode;

  @Id
  @ManyToOne(fetch= FetchType.EAGER)
  private Family family;

  @Id
  @Column(name="user_email", columnDefinition="VARCHAR(70)", nullable=false)
  private String userEmail;

  @Column(name="initial_role", columnDefinition="VARCHAR(10)", nullable = false)
  private Role role;

  @Column(name="created_at", columnDefinition = "TIMESTAMP")
  private Timestamp createdAt;

  @JsonIgnore
  private InviteCode inviteCodeObj;

  public MemberInvite() {}

  public MemberInvite(Family family, String userEmail) {
    this(family, userEmail, null, null);
  }

  public MemberInvite(Family family, String userEmail, Role role) {
    this(family, userEmail, role, null);
  }

  public MemberInvite(Family family, String userEmail, Role role, Timestamp created) {
    this(family, userEmail, role, created, null);
  }

  public MemberInvite(Family family, String userEmail, Role role, Timestamp created, String inviteCode) {
    this.family = family;
    this.userEmail = userEmail;
    this.role = role != null ? role : Role.lowestLevelRole();
    this.createdAt = created != null ? created : Timestamp.valueOf(LocalDateTime.now());
    InviteCode localInviteCode = null;
    if (inviteCode != null) {
      localInviteCode = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + inviteCode);
    } else {
      localInviteCode = new InviteCode(false);
    }
    this.inviteCode = localInviteCode.toString();

  }

  public InviteCode getInviteCode() {
    if (this.inviteCodeObj != null && !this.inviteCodeObj.equals(InviteCode.EMPTY)) {
      return inviteCodeObj;
    } else if (this.inviteCode != null) {
      InviteCode localInviteCode = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + this.inviteCode);
      this.inviteCodeObj = localInviteCode;
      return localInviteCode;
    }
    return null;
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
    if (this.inviteCodeObj != null && !this.inviteCodeObj.equals(InviteCode.EMPTY)) {
      this.inviteCodeObj.setCode(UUID.fromString(inviteCode));
    } else {
      InviteCode localInviteCode = new InviteCode();
      localInviteCode.setPersistent(true);
      localInviteCode.setCode(UUID.fromString(inviteCode));
    }
  }

  public void setInviteCode(InviteCode inviteCode) {
    this.inviteCode = inviteCode.toString();
    this.inviteCodeObj = inviteCode;
  }

  public UUID getInviteCodeUUID() {
    if (this.inviteCodeObj != null && !this.inviteCodeObj.equals(InviteCode.EMPTY)) {
      return this.inviteCodeObj.getCode();
    } else if (this.inviteCode != null) {
      return UUID.fromString(this.inviteCode);
    }
    return null;
  }

  public Long getFamilyId() {
    return family.getId();
  }

  public Family getFamily() {
    return family;
  }

  public void setFamily(Family family) {
    this.family = family;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }



  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MemberInvite that = (MemberInvite) o;
    return inviteCode.equals(that.inviteCode) && family.getId().equals(that.family.getId()) && userEmail.equals(that.userEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inviteCode, family, userEmail);
  }

  @Override
  public String toString() {
    return "MemberInvite{" +
        "inviteCode='" + inviteCode + '\'' +
        ", familyId=" + family.getId() +
        ", userEmail='" + userEmail + '\'' +
        ", role='" + role + '\'' +
        '}';
  }
}
