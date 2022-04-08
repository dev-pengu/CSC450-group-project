package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "poll_option")
public class PollOption implements Serializable {

  private static final long serialVersionUID = -7981596819052104857L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_option_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "value", columnDefinition = "VARCHAR(70)", nullable = false)
  private String value;

  @OneToMany(mappedBy = "vote", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  List<PollVote> votes;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id", referencedColumnName = "poll_id", columnDefinition = "BIGINT")
  private Poll poll;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<PollVote> getVotes() {
    return votes;
  }

  public void setVotes(List<PollVote> votes) {
    this.votes = votes;
  }

  public void addVote(PollVote vote) {
    if (this.votes == null) {
      this.votes = new ArrayList<>();
    }
    this.votes.add(vote);
  }

  public Poll getPoll() {
    return poll;
  }

  public void setPoll(Poll poll) {
    this.poll = poll;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PollOption other = (PollOption) obj;
    return Objects.equals(id, other.id) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    return "PollOption [id=" + id + ", value=" + value + ", votes=" + votes + "]";
  }

}
