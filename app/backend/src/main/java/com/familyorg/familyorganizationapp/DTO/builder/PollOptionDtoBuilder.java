package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.PollOptionDto;

public class PollOptionDtoBuilder implements DtoBuilder<PollOptionDto> {
  private Long id;
  private String value;
  private Integer votes;

  public PollOptionDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public PollOptionDtoBuilder withValue(String value) {
    this.value = value;
    return this;
  }

  public PollOptionDtoBuilder withVotes(Integer votes) {
    this.votes = votes;
    return this;
  }

  public PollOptionDtoBuilder addVote() {
    if (this.votes == null) {
      this.votes = 0;
    }
    this.votes++;
    return this;
  }

  @Override
  public PollOptionDto build() {
    return new PollOptionDto(id, value, votes);
  }

}
