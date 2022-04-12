package com.familyorg.familyorganizationapp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarEventDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.EventRepetitionDtoBuilder;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.domain.CalendarRepetitionFrequency;
import com.familyorg.familyorganizationapp.domain.EventRepetitionSchedule;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.CalendarEventRepository;
import com.familyorg.familyorganizationapp.repository.CalendarRepository;
import com.familyorg.familyorganizationapp.repository.EventRepetitionRepository;
import com.familyorg.familyorganizationapp.repository.RecurringCalendarEventRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.DateUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CalendarServiceImplTest {
  private CalendarServiceImpl calendarService;
  private CalendarEventRepository eventRepository;
  private EventRepetitionRepository scheduleRepository;
  private RecurringCalendarEventRepository recurringEventRepository;

  private UserService userService;
  private FamilyService familyService;

  private CalendarRepository calendarRepository;

  private static Map<Long, Calendar> testCalendars;
  private static Map<Long, CalendarEvent> testCalendarEvents;
  private static Map<Long, EventRepetitionSchedule> testRepetitionSchedules;
  private static Map<Long, RecurringCalendarEvent> testRecurringEvents;

  private static Long nextCalendarId = 1l;
  private static Long nextEventId = 1l;
  private static Long nextRepetitionId = 1l;
  private static Long nextRecurringId = 1l;

  static User USER_1 =
      new User(1l, "Test", "User", "testuser", "password", "testuser@test.com", "US/Central", null);
  static Family FAMILY_1 = new Family(1l, "Test Family 1", "000000", "America/Chicago", null, null);
  static List<FamilyMembers> familyOneMembers;

  @BeforeAll
  public static void setup() {
    testCalendars = new HashMap<>();
    testCalendarEvents = new HashMap<>();
    testRepetitionSchedules = new HashMap<>();
    testRecurringEvents = new HashMap<>();
  }

  @BeforeEach
  public void before() {
    familyOneMembers = new ArrayList<>();
    familyOneMembers.add(new FamilyMembers(USER_1, FAMILY_1, Role.OWNER, "e802d7"));
    FAMILY_1.setMembers(familyOneMembers.stream().collect(Collectors.toSet()));

    eventRepository = mock(CalendarEventRepository.class);
    scheduleRepository = mock(EventRepetitionRepository.class);
    recurringEventRepository = mock(RecurringCalendarEventRepository.class);
    userService = mock(UserService.class);
    familyService = mock(FamilyService.class);
    calendarRepository = mock(CalendarRepository.class);

    calendarService = new CalendarServiceImpl(calendarRepository,
        eventRepository, scheduleRepository,
        recurringEventRepository, userService,
        familyService);
    when(eventRepository.save(any(CalendarEvent.class))).thenAnswer(invocation -> {
      CalendarEvent event = invocation.getArgument(0);
      if (event.getId() == null) {
        event.setId(nextEventId);
        nextEventId++;
      }
      testCalendarEvents.put(event.getId(), event);
      return event;
    });
    when(calendarRepository.save(any(Calendar.class))).thenAnswer(invocation -> {
      Calendar calendar = invocation.getArgument(0);
      if (calendar.getId() == null) {
        calendar.setId(nextCalendarId);
        nextCalendarId++;
      }
      testCalendars.put(calendar.getId(), calendar);
      return calendar;
    });
    when(recurringEventRepository.save(any(RecurringCalendarEvent.class)))
        .thenAnswer(invocation -> {
          RecurringCalendarEvent event = invocation.getArgument(0);
          if (event.getId() == null) {
            event.setId(nextRecurringId);
            nextRecurringId++;
          }
          testRecurringEvents.put(event.getId(), event);
          return event;
        });
    when(scheduleRepository.save(any(EventRepetitionSchedule.class))).thenAnswer(invocation -> {
      EventRepetitionSchedule schedule = invocation.getArgument(0);
      if (schedule.getId() == null) {
        schedule.setId(nextRepetitionId);
        nextRepetitionId++;
      }
      testRepetitionSchedules.put(schedule.getId(), schedule);
      return schedule;
    });

    doNothing().when(eventRepository).deleteById(any(Long.class));
    doNothing().when(calendarRepository).deleteById(any(Long.class));
    doNothing().when(recurringEventRepository).removeRecurringByOriginatingId(any(Long.class));
    doNothing().when(scheduleRepository).deleteById(any(Long.class));

    when(eventRepository.findById(any(Long.class))).thenAnswer(invocation -> {
      CalendarEvent event = testCalendarEvents.get(invocation.getArgument(0));
      if (event == null)
        return Optional.empty();
      return Optional.of(event);
    });
    when(calendarRepository.findById(any(Long.class))).thenAnswer(invocation -> {
      Calendar calendar = testCalendars.get(invocation.getArgument(0));
      if (calendar == null)
        return Optional.empty();
      return Optional.of(calendar);
    });
    when(recurringEventRepository.findById(any(Long.class))).thenAnswer(invocation -> {
      RecurringCalendarEvent event = testRecurringEvents.get(invocation.getArgument(0));
      if (event == null)
        return Optional.empty();
      return Optional.of(event);
    });
    when(scheduleRepository.findById(any(Long.class))).thenAnswer(invocation -> {
      EventRepetitionSchedule schedule = testRepetitionSchedules.get(invocation.getArgument(0));
      if (schedule == null)
        return Optional.empty();
      return Optional.of(schedule);
    });
    when(userService.getRequestingUser()).thenReturn(USER_1);
    when(familyService.verfiyMinimumRoleSecurity(any(Family.class), any(User.class),
        any(Role.class)))
            .thenReturn(Boolean.TRUE);
    when(familyService.getFamilyById(any(Long.class))).thenReturn(Optional.of(FAMILY_1));
  }

  @Test
  @Order(1)
  public void test_calendar_creation() {
    /* Given */
    CalendarDto request = new CalendarDtoBuilder().withFamily(FAMILY_1.getId())
        .withDescription("Test Calendar")
        .build();

    /* When */
    calendarService.createCalendar(request);

    /* Then */
    Calendar addedCalendar = testCalendars.get(nextCalendarId - 1);
    assertNotNull(addedCalendar);
  }

  @Test
  @Order(2)
  public void test_calendar_updated() {
    /* Given */
    CalendarDto request = new CalendarDtoBuilder().withId(1l)
        .withFamily(FAMILY_1.getId())
        .withDescription("Updated Test Calendar")
        .build();

    /* When */
    calendarService.updateCalendar(request);

    /* Then */
    Calendar updatedCalendar = testCalendars.get(request.getId());
    assertEquals(request.getDescription(), updatedCalendar.getDescription());
  }

  @Test
  @Order(3)
  public void test_add_calendar_event() {
    /* Given */
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.DAY_OF_YEAR, 6);
    String requestDate = DateUtil.toTimezone(Date.from(cal.toInstant()),
        TimeZone.getTimeZone("America/Chicago"), TimeZone.getTimeZone("America/Chicago"));
    CalendarEventDto request = new CalendarEventDtoBuilder().withCalendarId(1l)
        .setIsAllDay(true)
        .setIsFamilyEvent(true)
        .withStartDate(requestDate)
        .withDescription("Test event")
        .withEndDate(requestDate)
        .withNotes("")
        .build();

    /* When */
    calendarService.addEvent(request);

    /* Then */
    CalendarEvent addedEvent = testCalendarEvents.get(1l);
    assertNotNull(addedEvent);
    assertEquals(request.getDescription(), addedEvent.getDescription());
    assertEquals(request.getCalendarId(), addedEvent.getCalendar().getId());
    assertEquals(request.isAllDay(), addedEvent.isAllDay());
    assertEquals(request.isFamilyEvent(), addedEvent.isFamilyEvent());
    assertEquals(request.getNotes(), addedEvent.getNotes());
    assertEquals(USER_1.getUsername(), addedEvent.getCreatedBy().getUsername());
    assertNotNull(addedEvent.getCreatedBy());
    assertNotNull(addedEvent.getId());
    assertNotNull(addedEvent.getTimezone());
  }

  @Test
  @Order(4)
  public void test_add_calendar_event_with_recurring_schedule() {
    /* Given */
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.DAY_OF_YEAR, 6);
    String requestDate = DateUtil.toTimezone(Date.from(cal.toInstant()),
        TimeZone.getTimeZone("America/Chicago"), TimeZone.getTimeZone("America/Chicago"));
    CalendarEventDto request = new CalendarEventDtoBuilder().withCalendarId(1l)
        .setIsAllDay(false)
        .setIsFamilyEvent(false)
        .withStartDate(requestDate)
        .withEndDate(requestDate)
        .withDescription("Test Recurring Event")
        .withNotes("")
        .withRepetitionSchedule(
            new EventRepetitionDtoBuilder().withFrequency(CalendarRepetitionFrequency.MONTHLY)
                .withInterval(1)
                .withCount(3)
                .build())
        .build();

    /* When */
    calendarService.addEvent(request);

    /* Then */
    CalendarEvent addedEvent = testCalendarEvents.get(2l);
    assertNotNull(addedEvent);
    assertEquals(request.getDescription(), addedEvent.getDescription());
    assertEquals(request.isAllDay(), addedEvent.isAllDay());
    assertEquals(request.isFamilyEvent(), addedEvent.isFamilyEvent());
    assertEquals(request.getNotes(), addedEvent.getNotes());
    assertEquals(USER_1.getUsername(), addedEvent.getCreatedBy().getUsername());
    assertNotNull(addedEvent.getCreatedBy());
    assertNotNull(addedEvent.getId());
    assertNotNull(addedEvent.getTimezone());
    assertNotNull(addedEvent.getRecurringEventInfo());
    assertNotNull(addedEvent.getEventRepetitionSchedule());
  }

  @Order(5)
  @Test
  public void test_update_non_recurring_event() {
    /* Given */
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.DAY_OF_YEAR, 6);
    String requestDate = DateUtil.toTimezone(Date.from(cal.toInstant()),
        TimeZone.getTimeZone("America/Chicago"), TimeZone.getTimeZone("America/Chicago"));
    CalendarEventDto request = new CalendarEventDtoBuilder().withCalendarId(1l)
        .setIsAllDay(true)
        .setIsFamilyEvent(true)
        .withStartDate(requestDate)
        .withDescription("Updated Test event")
        .withEndDate(requestDate)
        .withNotes("")
        .withId(1l)
        .setRecurringEvent(false)
        .withRecurringId(null)
        .build();

    /* When */
    calendarService.updateEvent(request);

    /* Then */
    CalendarEvent updatedEvent = testCalendarEvents.get(1l);
    assertNotNull(updatedEvent);
    assertEquals(request.getDescription(), updatedEvent.getDescription());
  }

  @Order(6)
  @Test
  public void test_update_recurring_event() {
    /* Given */
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.add(java.util.Calendar.DAY_OF_YEAR, 6);
    String requestDate = DateUtil.toTimezone(Date.from(cal.toInstant()),
        TimeZone.getTimeZone("America/Chicago"), TimeZone.getTimeZone("America/Chicago"));
    CalendarEventDto request =
        new CalendarEventDtoBuilder().withRecurringId(1l)
            .setRecurringEvent(true)
            .setIsAllDay(false)
            .setIsFamilyEvent(false)
            .withStartDate(requestDate)
            .withEndDate(requestDate)
            .withDescription("Test Recurring Event")
            .withNotes("")
            .withId(null)
            .build();

    /* When */
    calendarService.updateEvent(request);

    /* Then */
    CalendarEvent event = testCalendarEvents.get(3l);
    assertNotNull(event);
    assertTrue(
        DateUtil.parseDateTime(request.getStartDate()).compareTo(event.getStartDatetime()) == 0);
    assertTrue(
        DateUtil.parseDateTime(request.getEndDate()).compareTo(event.getEndDatetime()) == 0);
  }

  @Order(6)
  @Test
  public void test_get_calendar_data() {
    /* Given */
    when(calendarRepository.calendarDataByFamilyIds(any(List.class)))
        .thenReturn(Collections.singletonList(testCalendars.get(1l)));
    when(calendarRepository.search(any(List.class), any(List.class), any(Set.class)))
        .thenReturn(Collections.singletonList(testCalendars.get(1l)));
    when(familyService.getFamiliesByUser(any(String.class)))
        .thenReturn(Collections.singletonList(FAMILY_1));
    when(eventRepository.getEventsByCalendarIdsInDateRange(any(Set.class), any(Timestamp.class),
        any(Timestamp.class), any(Set.class)))
            .thenAnswer(invocation -> {
              List<CalendarEvent> events = testCalendarEvents.values()
                  .stream()
                  .filter(event -> event.getCalendar().getId().equals(1l))
                  .collect(Collectors.toList());
              Map<Long, List<CalendarEvent>> response = new HashMap<>();
              events.forEach(event -> {
                response.putIfAbsent(event.getCalendar().getId(), new ArrayList<>());
                response.get(event.getCalendar().getId()).add(event);
              });
              return response;
            });
    when(recurringEventRepository.getEventsByCalendarIdsInDateRange(any(Set.class),
        any(Timestamp.class), any(Timestamp.class), any(Set.class)))
            .thenAnswer(invocation -> {
              List<RecurringCalendarEvent> events = testRecurringEvents.values()
                  .stream()
                  .filter(event -> event.getOriginatingEvent().getCalendar().getId().equals(1l))
                  .collect(Collectors.toList());
              Map<Long, List<RecurringCalendarEvent>> response = new HashMap<>();
              events.forEach(event -> {
                response.putIfAbsent(event.getOriginatingEvent().getCalendar().getId(),
                    new ArrayList<>());
                response.get(event.getOriginatingEvent().getCalendar().getId()).add(event);
              });
              return response;
            });
    CalendarSearchRequestDto request = new CalendarSearchRequestDto();
    request.setStart(DateUtil.parseDate("2022-04-01"));
    request.setEnd(DateUtil.parseDate("2022-04-30"));

    /* When */
    CalendarSearchResponseDto response = calendarService.search(request);

    /* Then */
    assertNotNull(response);
    assertTrue(response.getCalendars().size() > 0);
    CalendarDto calendar = response.getCalendars().get(0);
    assertEquals(1l, calendar.getId());
    assertEquals("Updated Test Calendar", calendar.getDescription());
    assertFalse(calendar.getEvents().isEmpty());
  }

  @Order(7)
  @Test
  public void test_get_event() {
    /* Given */
    Long id = 1l;

    /* When */
    CalendarEventDto response = calendarService.getEvent(id);

    /* Then */
    assertNotNull(response);
    assertEquals(id, response.getId());
    assertNotNull(response.isAllDay());
    assertNotNull(response.isFamilyEvent());
    assertNotNull(response.getCalendarId());
    assertNotNull(response.getColor());
    assertNotNull(response.getCreated());
    assertNotNull(response.getCreatedBy());
    assertNotNull(response.getStartDate());
    assertNotNull(response.getEndDate());
    assertNotNull(response.getDescription());
  }

  @Order(8)
  @Test
  public void test_get_recurring_event() {
    /* Given */
    Long id = 1l;

    /* When */
    CalendarEventDto response = calendarService.getRecurringEvent(id);

    /* Then */
    assertNotNull(response);
    assertEquals(id, response.getRecurringId());
    assertTrue(response.isRecurringEvent());
    assertNotNull(response.isAllDay());
    assertNotNull(response.isFamilyEvent());
    assertNotNull(response.getCalendarId());
    assertNotNull(response.getCreated());
    assertNotNull(response.getCreatedBy());
    assertNotNull(response.getStartDate());
    assertNotNull(response.getEndDate());
    assertNotNull(response.getDescription());
    assertNotNull(response.getRepetitionSchedule());
  }

  @Order(9)
  @Test
  public void test_delete_recurring_event() {
    /* Given */
    Long id = 1l;

    /* When */
    calendarService.deleteRecurringEvent(id);

    /* Then */
    // if no errors are thrown, the test passes
  }

  @Order(10)
  @Test
  public void test_delete_event() {
    /* Given */
    Long id = 1l;
    boolean removeRecurring = false;

    /* When */
    calendarService.deleteEvent(id, removeRecurring);

    /* Then */
    // if no errors are thrown, the test passes
  }

  @Order(11)
  @Test
  public void test_delete_event_and_all_occurrences() {
    /* Given */
    Long id = 1l;
    boolean removeRecurring = true;

    /* When */
    calendarService.deleteEvent(id, removeRecurring);

    /* Then */
    // if no errors are thrown, the test passes
  }

  @Order(12)
  @Test
  public void test_delete_calendar() {
    /* Given */
    Long id = 1l;

    /* When */
    calendarService.deleteCalendar(id);

    /* Then */
    // if no errors are thrown, the test passes
  }
}
