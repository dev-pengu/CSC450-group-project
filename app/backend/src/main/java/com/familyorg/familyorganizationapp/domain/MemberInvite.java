package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.familyorg.familyorganizationapp.domain.id.MemberInviteId;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "member_invite")
@IdClass(MemberInviteId.class)
public class MemberInvite implements Serializable {

  private static final long serialVersionUID = 4046233503515534959L;

  @JsonIgnore
  @Id
  @Column(name = "invite_code", columnDefinition = "VARCHAR(36)", nullable = false)
  private String inviteCode;

  @JsonIgnore
  @Id
  @ManyToOne(fetch = FetchType.EAGER)
  private Family family;

  @Id
  @Column(name = "user_email", columnDefinition = "VARCHAR(70)", nullable = false)
  private String userEmail;

  @Column(name = "initial_role", columnDefinition = "INT", nullable = false)
  private Role role;

  @Column(name = "created_at", columnDefinition = "TIMESTAMP")
  private Timestamp createdAt;

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

  public MemberInvite(Family family, String userEmail, Role role, Timestamp created,
      String inviteCode) {
    this.family = family;
    this.userEmail = userEmail;
    this.role = role != null ? role : Role.lowestLevelRole();
    this.createdAt = created != null ? created : Timestamp.valueOf(LocalDateTime.now());
    if (this.inviteCode != null) {
      this.inviteCode = inviteCode;
    } else {
      this.inviteCode = new InviteCode(false).toString();
    }
  }

  public String getInviteCode() {
    return this.inviteCode;
  }

  public InviteCode getInviteCodeObj() {
    if (inviteCode == null) {
      return new InviteCode();
    }
    return InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + this.inviteCode);
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public void setInviteCode(InviteCode inviteCode) {
    this.inviteCode = inviteCode.toString();
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
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    MemberInvite that = (MemberInvite) o;
    return inviteCode.equals(that.inviteCode) && family.getId().equals(that.family.getId())
        && userEmail.equals(that.userEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inviteCode, family, userEmail);
  }

  @Override
  public String toString() {
    return "MemberInvite{" + "inviteCode='" + inviteCode + '\'' + ", familyId=" + family.getId()
        + ", userEmail='" + userEmail + '\'' + ", role='" + role + '\'' + '}';
  }
}
