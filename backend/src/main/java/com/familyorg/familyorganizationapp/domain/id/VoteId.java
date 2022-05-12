package com.familyorg.familyorganizationapp.domain.id;

import java.io.Serializable;
import java.util.Objects;

public class VoteId implements Serializable {

  private static final long serialVersionUID = -874459061482088726L;

  private Long poll;
  private Long user;

  public VoteId() {
    super();
  }

  public VoteId(Long pollId, Long userId) {
    super();
    this.poll = pollId;
    this.user = userId;
  }

  public Long getPollId() {
    return poll;
  }

  public void setPollId(Long pollId) {
    this.poll = pollId;
  }

  public Long getUserId() {
    return user;
  }

  public void setUserId(Long userId) {
    this.user = userId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(poll, user);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    VoteId other = (VoteId) obj;
    return Objects.equals(poll, other.poll) && Objects.equals(user, other.user);
  }


}
