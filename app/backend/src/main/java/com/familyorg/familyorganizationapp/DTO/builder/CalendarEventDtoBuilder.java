package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.Date;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.EventRepetitionDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;

public class CalendarEventDtoBuilder implements DtoBuilder<CalendarEventDto> {
  private Long id;
  private Boolean isAllDay = false;
  private Boolean isFamilyEvent = false;
  private String color;
  private String startDate;
  private String endDate;
  private EventRepetitionDto repetitionSchedule;
  private String description;
  private String notes;
  private UserDto createdBy;
  private Date created;
  private Long calendarId;
  private Boolean recurringEvent = false;
  private Long recurringId;
  private Boolean canEdit;

  public CalendarEventDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public CalendarEventDtoBuilder toggleAllDay() {
    this.isAllDay = !this.isAllDay;
    return this;
  }

  public CalendarEventDtoBuilder setIsAllDay(Boolean isAllDay) {
    this.isAllDay = isAllDay;
    return this;
  }

  public CalendarEventDtoBuilder setIsFamilyEvent(Boolean isFamilyEvent) {
    this.isFamilyEvent = isFamilyEvent;
    return this;
  }

  public CalendarEventDtoBuilder withColor(String color) {
    this.color = color;
    return this;
  }

  public CalendarEventDtoBuilder withStartDate(String startDate) {
    this.startDate = startDate;
    return this;
  }

  public CalendarEventDtoBuilder withEndDate(String endDate) {
    this.endDate = endDate;
    return this;
  }

  public CalendarEventDtoBuilder withRepetitionSchedule(EventRepetitionDto repetition) {
    this.repetitionSchedule = repetition;
    return this;
  }

  public CalendarEventDtoBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public CalendarEventDtoBuilder withNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public CalendarEventDtoBuilder withCreator(UserDto creator) {
    this.createdBy = creator;
    return this;
  }

  public CalendarEventDtoBuilder withCreatedDate(Date created) {
    this.created = created;
    return this;
  }

  public CalendarEventDtoBuilder withCalendarId(Long id) {
    this.calendarId = id;
    return this;
  }

  public CalendarEventDtoBuilder setRecurringEvent(Boolean isRecurring) {
    this.recurringEvent = isRecurring;
    return this;
  }

  public CalendarEventDtoBuilder withRecurringId(Long id) {
    this.recurringId = id;
    return this;
  }

  public CalendarEventDtoBuilder setCanEdit(Boolean canEdit) {
    this.canEdit = canEdit;
    return this;
  }

  @Override
  public CalendarEventDto build() {
    return new CalendarEventDto(id, isAllDay, isFamilyEvent, color, startDate, endDate,
        repetitionSchedule, description, notes, createdBy, created, calendarId, recurringEvent,
        recurringId, canEdit);
  }
}
