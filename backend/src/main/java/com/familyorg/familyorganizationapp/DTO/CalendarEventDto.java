package com.familyorg.familyorganizationapp.DTO;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class CalendarEventDto {
  private final Long id;
  private Boolean allDay = false;
  private Boolean familyEvent = false;

  @JsonInclude(Include.NON_NULL)
  private final String color;

  private final String startDate;
  private final String endDate;
  private final EventRepetitionDto repetitionSchedule;
  private final String description;
  private final String notes;
  private final UserDto createdBy;
  private final Date created;
  private final Long calendarId;
  private Boolean recurringEvent = false;
  private final Long recurringId;

  @JsonInclude(Include.NON_NULL)
  private Boolean detachEvent = false;

  private final Boolean userCanEdit;

  @JsonInclude(Include.NON_EMPTY)
  private final Set<ColorDto> assignees;

  public CalendarEventDto(
      Long id,
      Boolean isAllDay,
      Boolean familyEvent,
      String color,
      String startDate,
      String endDate,
      EventRepetitionDto repetitionSchedule,
      String description,
      String notes,
      UserDto createdBy,
      Date created,
      Long calendarId,
      Boolean isRecurringEvent,
      Long recurringId,
      Boolean userCanEdit,
      Set<ColorDto> assignees,
      Boolean detachEvent) {
    super();
    this.id = id;
    this.allDay = isAllDay;
    this.familyEvent = familyEvent;
    this.color = color == null ? null : color.trim();
    this.startDate = startDate;
    this.endDate = endDate;
    this.repetitionSchedule = repetitionSchedule;
    this.description = description == null ? null : description.trim();
    this.notes = notes == null ? null : notes.trim();
    this.createdBy = createdBy;
    this.created = created;
    this.calendarId = calendarId;
    this.recurringEvent = isRecurringEvent;
    this.recurringId = recurringId;
    this.userCanEdit = userCanEdit;
    this.assignees = assignees;
    this.detachEvent = detachEvent;
  }

  public Long getId() {
    return id;
  }

  public Boolean isAllDay() {
    return allDay;
  }

  public Boolean isFamilyEvent() {
    return familyEvent;
  }

  public String getColor() {
    return color;
  }

  public String getStartDate() {
    return startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public EventRepetitionDto getRepetitionSchedule() {
    return repetitionSchedule;
  }

  public String getDescription() {
    return description;
  }

  public String getNotes() {
    return notes;
  }

  public UserDto getCreatedBy() {
    return createdBy;
  }

  public Date getCreated() {
    return created;
  }

  public Long getCalendarId() {
    return calendarId;
  }

  public Boolean isRecurringEvent() {
    return recurringEvent;
  }

  public Long getRecurringId() {
    return recurringId;
  }

  public Boolean getUserCanEdit() {
    return userCanEdit;
  }

  public Set<ColorDto> getAssignees() {
    return assignees;
  }

  public Boolean getDetachEvent() {
    return detachEvent;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        color,
        created,
        createdBy,
        description,
        endDate,
        id,
        allDay,
        notes,
        repetitionSchedule,
        startDate,
        calendarId,
        familyEvent,
        recurringEvent,
        recurringId,
        userCanEdit);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    CalendarEventDto other = (CalendarEventDto) obj;
    return Objects.equals(color, other.color)
        && Objects.equals(created, other.created)
        && Objects.equals(createdBy, other.createdBy)
        && Objects.equals(description, other.description)
        && Objects.equals(endDate, other.endDate)
        && Objects.equals(id, other.id)
        && Objects.equals(allDay, other.allDay)
        && Objects.equals(notes, other.notes)
        && Objects.equals(repetitionSchedule, other.repetitionSchedule)
        && Objects.equals(startDate, other.startDate)
        && Objects.equals(calendarId, other.calendarId)
        && Objects.equals(familyEvent, other.familyEvent)
        && Objects.equals(recurringEvent, other.recurringEvent)
        && Objects.equals(recurringId, other.recurringId)
        && Objects.equals(userCanEdit, other.userCanEdit);
  }

  @Override
  public String toString() {
    return "CalendarEventDto [id="
        + id
        + ", isAllDay="
        + allDay
        + ", isFamilyEvent="
        + familyEvent
        + ", color="
        + color
        + ", startDate="
        + startDate
        + ", endDate="
        + endDate
        + ", repetitionSchedule="
        + repetitionSchedule
        + ", description="
        + description
        + ", notes="
        + notes
        + ", createdBy="
        + createdBy
        + ", created="
        + created
        + ", calendarId="
        + calendarId
        + ", isRecurringEvent="
        + recurringEvent
        + ", recurringId="
        + recurringId
        + ", userCanEdit="
        + userCanEdit
        + ", detachEvent="
        + detachEvent
        + ", assignees"
        + assignees
        + "]";
  }
}
