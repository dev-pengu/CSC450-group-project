package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.ArrayList;
import java.util.List;
import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollOptionDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.PollVote;

public class PollDtoBuilder implements DtoBuilder<PollDto> {

  private Long id;
  private Long familyId;
  private UserDto createdBy;
  private String createdDateTime;
  private String description;
  private String notes;
  private String closedDateTime;
  private Boolean closed;
  private List<PollOptionDto> options;
  private List<UserDto> respondents;
  private Long vote;
  private String familyName;

  public PollDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public PollDtoBuilder withFamilyId(Long id) {
    this.familyId = id;
    return this;
  }

  public PollDtoBuilder withCreatedBy(UserDto creator) {
    this.createdBy = creator;
    return this;
  }

  public PollDtoBuilder withCreatedDateTime(String created) {
    this.createdDateTime = created;
    return this;
  }

  public PollDtoBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public PollDtoBuilder withNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public PollDtoBuilder withClosedDateTime(String closedDate) {
    this.closedDateTime = closedDate;
    return this;
  }

  public PollDtoBuilder isClosed(Boolean closed) {
    this.closed = closed;
    return this;
  }

  public PollDtoBuilder withOptions(List<PollOptionDto> options) {
    this.options = options;
    return this;
  }

  public PollDtoBuilder addOption(PollOptionDto option) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(option);
    return this;
  }

  public PollDtoBuilder withRespondents(List<UserDto> respondents) {
    this.respondents = respondents;
    return this;
  }

  public PollDtoBuilder addRespondent(UserDto respondent) {
    if (this.respondents == null) {
      this.respondents = new ArrayList<>();
    }
    this.respondents.add(respondent);
    return this;
  }

  public PollDtoBuilder setVote(Long vote) {
    this.vote = vote;
    return this;
  }

  public PollDtoBuilder setVote(PollVote vote) {
    if (vote != null) {
      if (vote.getVote() != null) {
        this.vote = vote.getVote().getId();
        return this;
      }
    }
    return this;
  }

  public PollDtoBuilder setFamilyName(String name) {
    this.familyName = name;
    return this;
  }

  @Override
  public PollDto build() {
    return new PollDto(id, familyId, createdBy, createdDateTime, description, notes, closedDateTime,
        closed, options, respondents, null, vote, familyName);
  }

}
