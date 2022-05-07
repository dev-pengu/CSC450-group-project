package com.familyorg.familyorganizationapp.DTO;

public class VoteDto {
  private Long pollId;
  private UserDto user;
  private PollOptionDto choice;

  public VoteDto(Long pollId, UserDto user, PollOptionDto choice) {
    super();
    this.pollId = pollId;
    this.user = user;
    this.choice = choice;
  }

  public Long getPollId() {
    return pollId;
  }

  public UserDto getUser() {
    return user;
  }

  public PollOptionDto getChoice() {
    return choice;
  }

  @Override
  public String toString() {
    return "VoteDto [pollId=" + pollId + ", user=" + user + ", choice=" + choice + "]";
  }
}
