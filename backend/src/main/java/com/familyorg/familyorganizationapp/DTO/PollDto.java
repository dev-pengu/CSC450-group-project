package com.familyorg.familyorganizationapp.DTO;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PollDto {
  private final Long id;
  private final Long familyId;
  private final UserDto createdBy;
  private final String createdDateTime;
  private final String description;
  private final String notes;
  private final String closedDateTime;
  private final Boolean closed;

  @JsonInclude(Include.NON_NULL)
  private final Boolean omitCreator;

  private final List<PollOptionDto> options;

  @JsonInclude(Include.NON_NULL)
  private final List<UserDto> respondents;

  @JsonInclude(Include.NON_NULL)
  private final Long vote;

  @JsonInclude(Include.NON_NULL)
  private final String familyName;

  public PollDto(
      Long id,
      Long familyId,
      UserDto createdBy,
      String createdDateTime,
      String description,
      String notes,
      String closedDateTime,
      Boolean isClosed,
      List<PollOptionDto> options,
      List<UserDto> respondents,
      Boolean omitCreator,
      Long vote,
      String familyName) {
    super();
    this.id = id;
    this.familyId = familyId;
    this.createdBy = createdBy;
    this.createdDateTime = createdDateTime;
    this.description = description == null ? null : description.trim();
    this.notes = notes == null ? null : notes.trim();
    this.closedDateTime = closedDateTime;
    this.closed = isClosed;
    this.options = options == null ? new ArrayList<>() : options;
    this.respondents = respondents == null ? new ArrayList<>() : respondents;
    this.omitCreator = omitCreator;
    this.vote = vote;
    this.familyName = familyName == null ? null : familyName.trim();
  }

  public Long getId() {
    return id;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public UserDto getCreatedBy() {
    return createdBy;
  }

  public String getCreatedDateTime() {
    return createdDateTime;
  }

  public String getDescription() {
    return description;
  }

  public String getNotes() {
    return notes;
  }

  public String getClosedDateTime() {
    return closedDateTime;
  }

  public Boolean isClosed() {
    return closed;
  }

  public List<PollOptionDto> getOptions() {
    return options;
  }

  public List<UserDto> getRespondents() {
    return respondents;
  }

  public Boolean shouldOmitCreator() {
    return omitCreator;
  }

  public Long getVote() {
    return vote;
  }

  public String getFamilyName() {
    return familyName;
  }

  @Override
  public String toString() {
    return "PollDto [id="
        + id
        + ", familyId="
        + familyId
        + ", createdBy="
        + createdBy
        + ", createdDateTime="
        + createdDateTime
        + ", description="
        + description
        + ", notes="
        + notes
        + ", closedDateTime="
        + closedDateTime
        + ", isClosed="
        + closed
        + ", options="
        + options
        + "]";
  }
}
