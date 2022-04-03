package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchResponseDto;

public interface CalendarService {
  void createCalendar(CalendarDto calendar);

  void addEvent(CalendarEventDto event);

  void updateEvent(CalendarEventDto event);

  void deleteCalendar(Long id);

  void deleteEvent(Long id, boolean removeRecurring);

  CalendarSearchResponseDto search(CalendarSearchRequestDto calendarRequest);

  CalendarEventDto getEvent(Long id);

  CalendarEventDto getRecurringEvent(Long id);

  void updateCalendar(CalendarDto request);

  void deleteRecurringEvent(Long id);

  void addAssignee(CalendarEventDto request);

  void removeAssignee(CalendarEventDto request);

}
