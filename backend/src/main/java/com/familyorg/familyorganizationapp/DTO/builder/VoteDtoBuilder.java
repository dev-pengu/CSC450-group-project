package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.PollOptionDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.VoteDto;

public class VoteDtoBuilder implements DtoBuilder<VoteDto> {
  private Long pollId;
  private UserDto user;
  private PollOptionDto choice;

  public VoteDtoBuilder withPollId(Long id) {
    this.pollId = id;
    return this;
  }

  public VoteDtoBuilder withUser(UserDto user) {
    this.user = user;
    return this;
  }

  public VoteDtoBuilder withChoice(PollOptionDto choice) {
    this.choice = choice;
    return this;
  }

  @Override
  public VoteDto build() {
    return new VoteDto(pollId, user, choice);
  }


}
