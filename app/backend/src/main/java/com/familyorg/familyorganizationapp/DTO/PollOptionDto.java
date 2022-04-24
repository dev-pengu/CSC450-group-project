package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PollOptionDto {
  private Long id;
  private String value;
  @JsonInclude(Include.NON_NULL)
  private Integer votes;

  public PollOptionDto(Long id, String value, Integer votes) {
    super();
    this.id = id;
    this.value = value;
    this.votes = votes;
  }

  public Long getId() {
    return id;
  }

  public String getValue() {
    return value;
  }

  public Integer getVotes() {
    return votes;
  }

  @Override
  public String toString() {
    return "PollOptionDto [id=" + id + ", value=" + value + ", votes=" + votes + "]";
  }
}
