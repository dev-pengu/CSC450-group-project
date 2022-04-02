package com.familyorg.familyorganizationapp.DTO;

import java.util.List;

public class PollDto {
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

  public PollDto(Long id, Long familyId, UserDto createdBy, String createdDateTime,
      String description, String notes, String closedDateTime, Boolean isClosed,
      List<PollOptionDto> options, List<UserDto> respondents) {
    super();
    this.id = id;
    this.familyId = familyId;
    this.createdBy = createdBy;
    this.createdDateTime = createdDateTime;
    this.description = description;
    this.notes = notes;
    this.closedDateTime = closedDateTime;
    this.closed = isClosed;
    this.options = options;
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

  @Override
  public String toString() {
    return "PollDto [id=" + id + ", familyId=" + familyId + ", createdBy=" + createdBy
        + ", createdDateTime=" + createdDateTime + ", description=" + description + ", notes="
        + notes + ", closedDateTime=" + closedDateTime + ", isClosed=" + closed + ", options="
        + options + "]";
  }
}
