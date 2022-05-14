package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.EventRepetitionDto;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarEventDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.EventRepetitionDtoBuilder;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.service.CalendarService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class CalendarControllerTest {
  private CalendarService calendarService;
  private CalendarController calendarController;

  @BeforeEach
  public void setup() {
    calendarService = mock(CalendarService.class);
    calendarController = new CalendarController(calendarService);
  }

  @Test
  public void getCalendars_success() {
    /* Given */
    when(calendarService.getCalendars()).thenReturn(Collections.emptyList());

    /* When */
    ResponseEntity<List<CalendarDto>> response = calendarController.getCalendars();

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getCalendars_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).getCalendars();

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.getCalendars();
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getPotentialAssignees_success() {
    /* Given */
    when(calendarService.getPotentialAssignees(any(Long.class)))
        .thenReturn(Collections.emptyList());

    /* When */
    ResponseEntity<List<SearchFilter>> response = calendarController.getPotentialAssignees(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getPotentialAssignees_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).getPotentialAssignees(any(Long.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.getPotentialAssignees(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void createCalendar_success() {
    /* Given */
    doNothing().when(calendarService).createCalendar(any(CalendarDto.class));

    /* When */
    ResponseEntity<String> response =
        calendarController.createCalendar(new CalendarDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_createCalendar_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).createCalendar(any(CalendarDto.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.createCalendar(new CalendarDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void addEvent_success() {
    /* Given */
    doNothing().when(calendarService).addEvent(any(CalendarEventDto.class));

    /* When */
    ResponseEntity<String> response =
        calendarController.addEvent(new CalendarEventDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void updateCalendar_success() {
    /* Given */
    doNothing().when(calendarService).updateCalendar(any(CalendarDto.class));

    /* When */
    ResponseEntity<String> response =
        calendarController.updateCalendar(new CalendarDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateCalendar_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).updateCalendar(any(CalendarDto.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.updateCalendar(new CalendarDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateEvent_success() {
    /* Given */
    doNothing().when(calendarService).updateEvent(any(CalendarEventDto.class));

    /* When */
    ResponseEntity<String> response =
        calendarController.updateEvent(new CalendarEventDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateEvent_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).updateEvent(any(CalendarEventDto.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.updateEvent(new CalendarEventDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateRecurrenceSchedule_success() {
    /* Given */
    doNothing().when(calendarService).updateRecurringSchedule(any(EventRepetitionDto.class));

    /* When */
    ResponseEntity<String> response =
        calendarController.updateRecurrenceSchedule(new EventRepetitionDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateRecurrenceSchedule_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class)
        .when(calendarService)
        .updateRecurringSchedule(any(EventRepetitionDto.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.updateRecurrenceSchedule(new EventRepetitionDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteCalendar_success() {
    /* Given */
    doNothing().when(calendarService).deleteCalendar(any(Long.class));

    /* When */
    ResponseEntity<String> response = calendarController.deleteCalendar(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteCalendar_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).deleteCalendar(any(Long.class));

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.deleteCalendar(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteEvent_non_recurring_success() {
    /* Given */
    doNothing().when(calendarService).deleteEvent(any(Long.class), any(Boolean.class));

    /* When */
    ResponseEntity<String> response = calendarController.deleteEvent(1L, false, Optional.of(true));

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(calendarService, atLeastOnce()).deleteEvent(1L, true);
  }

  @Test
  public void deleteEvent_recurring_success() {
    /* Given */
    doNothing().when(calendarService).deleteRecurringEvent(any());

    /* When */
    ResponseEntity<String> response = calendarController.deleteEvent(1L, true, Optional.of(false));

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(calendarService, atLeastOnce()).deleteRecurringEvent(1L);
  }

  @Test
  public void when_deleteEvent_recurring_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).deleteRecurringEvent(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.deleteEvent(1L, true, Optional.empty());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getCalendarWithEvents_sucess() {
    /* Given */
    when(calendarService.search(any())).thenReturn(new CalendarSearchResponseDto());

    /* When */
    ResponseEntity<CalendarSearchResponseDto> response =
        calendarController.getCalendarWithEvents(new CalendarSearchRequestDto());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getCalendarWithEvents_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).search(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.getCalendarWithEvents(new CalendarSearchRequestDto());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getEvent_non_recurring_success() {
    /* Given */
    when(calendarService.getEvent(any())).thenReturn(new CalendarEventDtoBuilder().build());

    /* When */
    ResponseEntity<CalendarEventDto> response = calendarController.getEvent(1L, false);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(calendarService, atLeastOnce()).getEvent(1L);
  }

  @Test
  public void getEvent_recurring_success() {
    /* Given */
    when(calendarService.getEvent(any())).thenReturn(new CalendarEventDtoBuilder().build());

    /* When */
    ResponseEntity<CalendarEventDto> response = calendarController.getEvent(1L, true);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    verify(calendarService, atLeastOnce()).getRecurringEvent(1L);
  }

  @Test
  public void when_getEvent_non_recurring_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).getEvent(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.getEvent(1L, false);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void when_getEvent_recurring_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).getRecurringEvent(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.getEvent(1L, true);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void assignEvent_success() {
    /* Given */
    doNothing().when(calendarService).addAssignee(any());

    /* When */
    ResponseEntity<String> response =
        calendarController.assignEvent(new CalendarEventDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_assignEvent_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(calendarService).addAssignee(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          calendarController.assignEvent(new CalendarEventDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void unassignEvent_success() {
    /* Given */
    doNothing().when(calendarService).removeAssignee(any());

    /* When */
    ResponseEntity<String> response =
        calendarController.unassignEvent(new CalendarEventDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }
}
