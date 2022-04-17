package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.familyorg.familyorganizationapp.domain.id.VoteId;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "poll_vote")
@IdClass(VoteId.class)
public class PollVote implements Serializable {

  private static final long serialVersionUID = 6644419558395914349L;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_option_id", referencedColumnName = "poll_option_id",
      columnDefinition = "BIGINT", nullable = true)
  private PollOption vote;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User user;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", referencedColumnName = "poll_id", columnDefinition = "BIGINT")
  private Poll poll;

  public PollOption getVote() {
    return vote;
  }

  public void setVote(PollOption vote) {
    this.vote = vote;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Poll getPoll() {
    return poll;
  }

  public void setPoll(Poll poll) {
    this.poll = poll;
  }

  public VoteId getId() {
    return new VoteId(poll.getId(), user.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(poll, user, vote);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PollVote other = (PollVote) obj;
    return Objects.equals(poll, other.poll) && Objects.equals(user, other.user)
        && Objects.equals(vote, other.vote);
  }

  @Override
  public String toString() {
    return "PollVote [vote=" + vote.getId() + ", user=" + user + "]";
  }

}
