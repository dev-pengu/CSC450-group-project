package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class InviteCode implements Serializable {

  private static final long serialVersionUID = -1868348275730997549L;
  public static final String PERSISTENT_PREFIX = "PRS";
  public static final String ONE_TIME_USE_PREFIX = "OTU";
  public static final InviteCode EMPTY = new InviteCode();
  private UUID code;
  private boolean persistent;

  public InviteCode() {}

  public InviteCode(boolean persistent) {
    this.persistent = persistent;
    generate();
  }

  public void generate() {
    if (this.code == null) {
      this.code = UUID.randomUUID();
    }
  }

  public String getInviteCodeString() {
    if (code == null) {
      return null;
    }
    return (persistent ? PERSISTENT_PREFIX : ONE_TIME_USE_PREFIX) + "-" + code.toString();
  }

  public static InviteCode parseFromCodeString(String codeString) {
    Objects.requireNonNull(codeString);
    if (codeString.isBlank()) {
      throw new IllegalStateException("codeString passed did not contain enough parts");
    }

    String[] parts = codeString.trim().split("-", 2);
    if (parts.length < 2 || parts[1].length() < 36) {
      throw new IllegalStateException("codeString passed did not contain enough parts");
    }
    parts[1] = parts[1].trim();
    InviteCode inviteCode = new InviteCode();
    inviteCode.setPersistent(parts[0].contains(PERSISTENT_PREFIX));
    inviteCode.setCode(UUID.fromString(parts[1]));
    return inviteCode;
  }

  public UUID getCode() {
    return code;
  }

  public void setCode(UUID code) {
    this.code = code;
  }

  public boolean isPersistent() {
    return persistent;
  }

  public void setPersistent(boolean persistent) {
    this.persistent = persistent;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    InviteCode that = (InviteCode) o;
    return persistent == that.persistent && Objects.equals(code, that.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, persistent);
  }

  @Override
  public String toString() {
    return code.toString();
  }
}
