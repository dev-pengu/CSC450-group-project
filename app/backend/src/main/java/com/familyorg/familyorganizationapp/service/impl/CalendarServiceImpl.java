package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.Exception.ApiException;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.EventDateComparator;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.CalendarEventDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ColorDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.EventRepetitionDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.domain.CalendarRepetitionFrequency;
import com.familyorg.familyorganizationapp.domain.EventRepetitionSchedule;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.search.CalendarField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.repository.CalendarEventRepository;
import com.familyorg.familyorganizationapp.repository.CalendarRepository;
import com.familyorg.familyorganizationapp.repository.EventRepetitionRepository;
import com.familyorg.familyorganizationapp.repository.RecurringCalendarEventRepository;
import com.familyorg.familyorganizationapp.service.CalendarService;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.DateUtil;

@Service
public class CalendarServiceImpl implements CalendarService {

  private Logger logger = LoggerFactory.getLogger(CalendarServiceImpl.class);
  CalendarRepository calendarRepository;
  CalendarEventRepository eventRepository;
  EventRepetitionRepository scheduleRepository;
  RecurringCalendarEventRepository recurringEventRepository;

  UserService userService;
  FamilyService familyService;

  @Autowired
  public CalendarServiceImpl(
      CalendarRepository calendarRepository,
      CalendarEventRepository eventRepository,
      EventRepetitionRepository scheduleRepository,
      RecurringCalendarEventRepository recurringEventRepository,
      UserService userService,
      FamilyService familyService) {
    this.calendarRepository = calendarRepository;
    this.eventRepository = eventRepository;
    this.scheduleRepository = scheduleRepository;
    this.recurringEventRepository = recurringEventRepository;
    this.userService = userService;
    this.familyService = familyService;
  }

  @Override
  @Transactional
  public void createCalendar(CalendarDto request)
      throws UserNotFoundException, AuthorizationException, ResourceNotFoundException {
    if (request.getDescription() == null || request.getDescription().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Calendar description cannot be null.");
    }
    if (request.getFamilyId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Family id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());
    if (family.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getFamilyId() + " not found.");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");

    Calendar calendar = new Calendar();
    calendar.setDefault(false);
    calendar.setDescription(request.getDescription());
    calendar.setFamily(family.get());
    calendarRepository.save(calendar);
  }

  @Override
  @Transactional
  public void updateCalendar(CalendarDto request) {
    if (request.getId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Calendar id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Calendar> calendarOpt = calendarRepository.findById(request.getId());
    if (calendarOpt.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST,
          "Calendar with id " + request.getId() + " not found.");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            calendarOpt.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User not authorized to complete this actions.");
    Calendar calendar = calendarOpt.get();
    if (calendar.getDescription() != request.getDescription())
      calendar.setDescription(request.getDescription());
    calendarRepository.save(calendar);
  }

  @Override
  @Transactional
  public void addEvent(CalendarEventDto request) {
    User requestingUser = userService.getRequestingUser();
    if (request.getCalendarId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Calendar id cannot be null");
    }
    if (request.getDescription() == null || request.getDescription().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Description cannot be null or empty.");
    }
    if (request.getStartDate() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Start date cannot be null.");
    }
    if (request.getEndDate() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "End date cannot be null.");
    }
    if (DateUtil.parseDateTime(request.getStartDate()).compareTo(Date.from(Instant.now())) < 0) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "Start date occurs in the past.");
    }
    if (DateUtil.parseDateTime(request.getEndDate())
            .compareTo(DateUtil.parseDateTime(request.getStartDate()))
        < 0) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "End date occurs before start date.");
    }
    Optional<Calendar> calendar = calendarRepository.findById(request.getCalendarId());
    if (calendar.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST,
          "Calendar with id " + request.getCalendarId() + " not found.");
    Family family = calendar.get().getFamily();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(family, requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");

    CalendarEvent event = new CalendarEvent();
    event.setCalendar(calendar.get());
    event.setAllDay(request.isAllDay());
    event.setFamilyEvent(request.isFamilyEvent());
    event.setCreatedBy(requestingUser);
    event.setCreatedDatetime(Timestamp.from(Instant.now()));
    event.setDescription(request.getDescription());
    event.setNotes(request.getNotes());

    if (!request.isFamilyEvent()) {
      if (request.getAssignees() != null && !request.getAssignees().isEmpty()) {
        Set<User> assignees =
            request.getAssignees().stream()
                .map(assignee -> userService.getUserById(assignee.getUserId()))
                .collect(Collectors.toSet());
        event.setAssignees(assignees);
      }
      event.getAssignees().add(requestingUser);
    }

    TimeZone timezone = familyService.getUserTimeZoneOrDefault(requestingUser, family);
    event.setTimezone(timezone.getID());
    event.setStartDatetime(DateUtil.parseTimestamp(request.getStartDate()));
    event.setEndDatetime(DateUtil.parseTimestamp(request.getEndDate()));
    CalendarEvent savedEvent = eventRepository.save(event);
    if (request.getRepetitionSchedule() != null) {
      EventRepetitionSchedule repetitionSchedule =
          new EventRepetitionSchedule(savedEvent, request.getRepetitionSchedule());
      EventRepetitionSchedule savedSchedule = scheduleRepository.save(repetitionSchedule);
      savedEvent.setEventRepetitionSchedule(savedSchedule);
      savedEvent.setRecurringEventInfo(addRepeatingEvents(savedSchedule, savedEvent));
    }
    eventRepository.save(savedEvent);
  }

  @Override
  @Transactional
  public void updateEvent(CalendarEventDto request) {
    User requestingUser = userService.getRequestingUser();
    if ((request.getId() == null && !request.isRecurringEvent())
        && (request.isRecurringEvent() && request.getRecurringId() == null))
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Event identifier may not be null. Please provide either an id or if the event is recurring, specify as such and includ a recurring id.");

    // The request is to update a non recurring event
    if (!request.isRecurringEvent()) {
      Optional<CalendarEvent> eventOpt = eventRepository.findById(request.getId());
      if (eventOpt.isEmpty())
        throw new ResourceNotFoundException(
            ApiExceptionCode.CALENDAR_DOESNT_EXIST,
            "Calendar event with id " + request.getId() + " not found.");
      CalendarEvent event = eventOpt.get();
      Family family = event.getCalendar().getFamily();
      boolean hasAppropriatePermissions =
          familyService.verfiyMinimumRoleSecurity(family, requestingUser, Role.ADULT);
      if (!hasAppropriatePermissions)
        hasAppropriatePermissions =
            event.getCreatedBy().getUsername().equals(requestingUser.getUsername());
      if (!hasAppropriatePermissions)
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      TimeZone timezone =
          requestingUser.getTimezone() != null
              ? TimeZone.getTimeZone(requestingUser.getTimezone())
              : TimeZone.getTimeZone(family.getTimezone());

      // If there is a repetition schedule, we may need to update the recurring events
      if (request.getRepetitionSchedule() != null) {
        EventRepetitionSchedule newRepetitionSchedule =
            new EventRepetitionSchedule(event, request.getRepetitionSchedule());
        if (newRepetitionSchedule.getId() == event.getEventRepetitionSchedule().getId()) {
          if (newRepetitionSchedule.dataFieldsEquals(event.getEventRepetitionSchedule())) {
            // The repetition schedule isn't new and there has been a change, so just update the
            // values, and recalc recurring events
            scheduleRepository.save(newRepetitionSchedule);
            event.setEventRepetitionSchedule(newRepetitionSchedule);
            event.setRecurringEventInfo(updateFutureEvents(newRepetitionSchedule, event));
          }
        } else {
          // The repetition schedule is new, so we need to save it to the db, and add recurring
          // events
          EventRepetitionSchedule savedSchedule = scheduleRepository.save(newRepetitionSchedule);
          event.setEventRepetitionSchedule(savedSchedule);
          event.setRecurringEventInfo(addRepeatingEvents(savedSchedule, event));
        }
      }
      // update the event fields
      if (event.isAllDay() != request.isAllDay()) {
        event.setAllDay(request.isAllDay());
      }
      if (event.isFamilyEvent() != request.isFamilyEvent()) {
        event.setFamilyEvent(request.isFamilyEvent());
      }
      if (event.getDescription() != request.getDescription()) {
        event.setDescription(request.getDescription());
      }

      if (event.getEndDatetime().compareTo(DateUtil.parseTimestamp(request.getEndDate())) != 0) {
        event.setEndDatetime(DateUtil.parseTimestamp(request.getEndDate()));
      }
      if (event.getStartDatetime().compareTo(DateUtil.parseTimestamp(request.getStartDate()))
          != 0) {
        event.setStartDatetime(DateUtil.parseTimestamp(request.getStartDate()));
      }

      if (event.getNotes() != request.getNotes()) {
        event.setNotes(request.getNotes());
      }
      if (timezone.getID() != event.getTimezone()) {
        event.setTimezone(timezone.getID());
      }
      // save the event
      eventRepository.save(event);
    } else {
      // The request is to update (break from series) a recurring event so some different
      // steps are needed
      Optional<RecurringCalendarEvent> recurringEventOpt =
          recurringEventRepository.findById(request.getRecurringId());
      if (recurringEventOpt.isEmpty())
        throw new ResourceNotFoundException(
            ApiExceptionCode.EVENT_DOESNT_EXIST,
            "Recurring event with id " + request.getRecurringId() + " not found.");
      RecurringCalendarEvent recurringEvent = recurringEventOpt.get();
      CalendarEvent event = recurringEvent.getOriginatingEvent();
      Family family = event.getCalendar().getFamily();
      boolean hasAppropriatePermissions =
          familyService.verfiyMinimumRoleSecurity(family, requestingUser, Role.ADULT);
      if (!hasAppropriatePermissions)
        hasAppropriatePermissions =
            event.getCreatedBy().getUsername().equals(requestingUser.getUsername());
      if (!hasAppropriatePermissions)
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");

      breakRecurringEvent(recurringEvent, request);
    }
  }

  @Override
  @Transactional
  public void deleteCalendar(Long id) {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<Calendar> calendar = calendarRepository.findById(id);
    if (calendar.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST, "Calendar with id " + id + " not found.");
    if (calendar.get().isDefault())
      throw new AuthorizationException(
          ApiExceptionCode.ILLEGAL_ACTION_REQUESTED, "The default calendar may not be deleted.");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            calendar.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    calendarRepository.deleteById(id);
  }

  @Override
  @Transactional
  public void deleteEvent(Long id, boolean removeRecurring) {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<CalendarEvent> event = eventRepository.findById(id);
    if (event.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST, "Calendar event with id " + id + " not found.");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            event.get().getCalendar().getFamily(), requestingUser, Role.ADULT);
    if (!hasAppropriatePermissions)
      hasAppropriatePermissions =
          event.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    if (removeRecurring) {
      recurringEventRepository.removeRecurringByOriginatingId(id);
      eventRepository.deleteById(id);
    } else {
      RecurringCalendarEvent recurringEvent = recurringEventRepository.getFirstOccurrence(id);
      if (recurringEvent != null) {

        event.get().setStartDatetime(recurringEvent.getStartDatetime());
        event.get().setEndDatetime(recurringEvent.getEndDatetime());

        // recurringEventRepository.deleteById(recurringEvent.getId());
        event.get().getRecurringEventInfo().remove(recurringEvent);
        eventRepository.save(event.get());
      }
    }
  }

  @Override
  public List<CalendarDto> getCalendars() {
    User requestingUser = userService.getRequestingUser();
    List<Long> permittedFamilyIds = familyService.getFamilyIdsByUser(requestingUser.getUsername());
    List<Calendar> calendars = calendarRepository.search(permittedFamilyIds, null);
    return calendars.stream()
        .map(
            calendar ->
                new CalendarDtoBuilder()
                    .withId(calendar.getId())
                    .withDescription(calendar.getDescription())
                    .withFamily(calendar.getFamily().getId())
                  .setDefault(calendar.isDefault())
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<SearchFilter> getPotentialAssignees(Long id) {
    User requestingUser = userService.getRequestingUser();
    Optional<Calendar> calendar = calendarRepository.findById(id);
    if (calendar.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST, "Calendar with id " + id + " not found.");
    }
    Family family = calendar.get().getFamily();
    if (!family.isMember(requestingUser)) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_NOT_IN_FAMILY,
          "User is not a member of family associated with calendar.");
    }
    return family.getMembers().stream()
        .map(member -> new SearchFilter(member.getUser().getId(), member.getUser().getFullname()))
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteRecurringEvent(Long id) {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannt be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<RecurringCalendarEvent> event = recurringEventRepository.findById(id);
    if (event.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.EVENT_DOESNT_EXIST, "Recurring event with id " + id + " not found");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            event.get().getOriginatingEvent().getCalendar().getFamily(),
            requestingUser,
            Role.ADULT);
    if (!hasAppropriatePermissions)
      hasAppropriatePermissions =
          event
              .get()
              .getOriginatingEvent()
              .getCreatedBy()
              .getUsername()
              .equals(requestingUser.getUsername());
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    recurringEventRepository.removeById(id);
  }

  @Override
  public CalendarSearchResponseDto search(CalendarSearchRequestDto request) {
    verifyCalendarSearchRequest(request);
    if (request.getStart() == null && request.getEnd() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A search unlimited by date is not allowed.");
    }
    User requestingUser = userService.getRequestingUser();

    CalendarSearchResponseDto response = new CalendarSearchResponseDto();
    response.setStart(request.getStart());
    response.setEnd(request.getEnd());
    response.setActiveSearchFilters(request.getFilters());
    List<Long> permittedFamilyIds = familyService.getFamilyIdsByUser(requestingUser.getUsername());
    List<Long> requestFamilyIds = request.getIdsByField(CalendarField.FAMILY);

    List<Long> userIds =
        request.getIdsByField(CalendarField.USER).stream().collect(Collectors.toList());
    List<Long> calendarIds = request.getIdsByField(CalendarField.CALENDAR);

    List<Calendar> calendars =
        calendarRepository.search(
            requestFamilyIds.isEmpty()
                ? permittedFamilyIds
                : permittedFamilyIds.stream()
                    .filter(requestFamilyIds::contains)
                    .collect(Collectors.toList()),
            calendarIds.isEmpty() ? null : calendarIds);

    Set<Long> calendarIdsParam =
        calendars.stream().map(Calendar::getId).collect(Collectors.toSet());

    Timestamp startParam = new Timestamp(request.getStart().getTime());
    Timestamp endParam = new Timestamp(request.getEnd().getTime());
    Map<Long, List<CalendarEvent>> eventsByCalendar =
        eventRepository.getEventsByCalendarIdsInDateRange(
            calendarIdsParam, startParam, endParam, userIds);

    Map<Long, List<RecurringCalendarEvent>> recurringEventsByCalendar =
        recurringEventRepository.getEventsByCalendarIdsInDateRange(
            calendarIdsParam, startParam, endParam, userIds);

    response.setCalendars(
        buildResponseCalendars(
            calendars, eventsByCalendar, recurringEventsByCalendar, requestingUser, userIds));
    response.setSearchFilters(getSearchFilters(calendars));
    return response;
  }

  @Override
  public CalendarEventDto getEvent(Long id) {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();
    Optional<CalendarEvent> eventOpt = eventRepository.findById(id);
    if (eventOpt.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.CALENDAR_DOESNT_EXIST, "Calendar event with id " + id + " not found.");
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            eventOpt.get().getCalendar().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
    CalendarEvent event = eventOpt.get();
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, event.getCalendar().getFamily());

    return buildEventDtoFromCalendarEvent(event, timezone, requestingUser, Collections.emptyList());
  }

  @Override
  public CalendarEventDto getRecurringEvent(Long id) {
    if (id == null) {
      throw new BadRequestException(ApiExceptionCode.REQUIRED_PARAM_MISSING, "Id cannot be null.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<RecurringCalendarEvent> recurringEvent = recurringEventRepository.findById(id);
    if (recurringEvent.isEmpty())
      throw new ResourceNotFoundException(
          ApiExceptionCode.EVENT_DOESNT_EXIST, "Event with id " + id + " was not found.");
    CalendarEvent event = recurringEvent.get().getOriginatingEvent();
    boolean hasAppropriatePermissions =
        familyService.verfiyMinimumRoleSecurity(
            event.getCalendar().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePermissions)
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");

    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, event.getCalendar().getFamily());

    CalendarEventDto response =
        buildEventDtoFromRecurringEvent(
            recurringEvent.get(), timezone, requestingUser, Collections.emptyList());
    return response;
  }

  private List<CalendarDto> buildResponseCalendars(
      List<Calendar> calendars,
      Map<Long, List<CalendarEvent>> eventsByCalendar,
      Map<Long, List<RecurringCalendarEvent>> recurringEventsByCalendar,
      User requestingUser,
      List<Long> userIds) {
    calendars.stream()
        .forEach(
            calendar -> {
              calendar.setEvents(
                  eventsByCalendar.getOrDefault(calendar.getId(), Collections.emptyList()));
            });
    return calendars.stream()
        .map(
            calendar -> {
              TimeZone timezone =
                  familyService.getUserTimeZoneOrDefault(requestingUser, calendar.getFamily());
              List<CalendarEventDto> events =
                  calendar.getEvents().stream()
                      .map(
                          event ->
                              buildEventDtoFromCalendarEvent(
                                  event, timezone, requestingUser, userIds))
                      .collect(Collectors.toList());
              List<CalendarEventDto> recurringEvents =
                  buildRecurringEventsForResponse(
                      calendar, recurringEventsByCalendar, timezone, requestingUser, userIds);

              events.addAll(recurringEvents);
              Collections.sort(events, new EventDateComparator());

              return new CalendarDtoBuilder()
                  .withDescription(calendar.getDescription())
                  .withId(calendar.getId())
                  .withFamily(calendar.getFamily())
                  .withEvents(events)
                  .setDefault(calendar.isDefault())
                  .withColor(calendar.getFamily().getEventColor())
                  .build();
            })
        .collect(Collectors.toList());
  }

  private List<CalendarEventDto> buildRecurringEventsForResponse(
      Calendar calendar,
      Map<Long, List<RecurringCalendarEvent>> recurringEventsByCalendar,
      TimeZone timezone,
      User requestingUser,
      List<Long> userIds) {
    List<CalendarEventDto> recurringEvents = new LinkedList<>();
    List<RecurringCalendarEvent> re = recurringEventsByCalendar.get(calendar.getId());
    if (re != null && re.size() > 0) {
      re.forEach(
          recurringEvent -> {
            recurringEvents.add(
                buildEventDtoFromRecurringEvent(recurringEvent, timezone, requestingUser, userIds));
          });
    }
    return recurringEvents;
  }

  private CalendarEventDto buildEventDtoFromCalendarEvent(
      CalendarEvent event, TimeZone timezone, User requestingUser, List<Long> userIds) {
    Map<Long, String> colors =
        event.getCalendar().getFamily().getMembers().stream()
            .collect(
                Collectors.toMap(
                    m -> m.getUser().getId(),
                    m ->
                        m.getEventColor() != null
                            ? m.getEventColor()
                            : m.getFamily().getEventColor()));
    return new CalendarEventDtoBuilder()
        .withId(event.getId())
        .withCalendarId(event.getCalendar().getId())
        .setIsAllDay(event.isAllDay())
        .setIsFamilyEvent(event.isFamilyEvent())
        .withColor(event.isFamilyEvent() ? event.getCalendar().getFamily().getEventColor() : null)
        .withCreatedDate(event.getCreatedDatetime())
        .withCreator(UserDto.fromUserObj(event.getCreatedBy()))
        .withEndDate(
            DateUtil.toTimezone(
                event.getEndDatetime(), TimeZone.getTimeZone(event.getTimezone()), timezone))
        .withStartDate(
            DateUtil.toTimezone(
                event.getStartDatetime(), TimeZone.getTimeZone(event.getTimezone()), timezone))
        .withNotes(event.getNotes())
        .withDescription(event.getDescription())
        .withRepetitionSchedule(
            event.getEventRepetitionSchedule() == null
                ? null
                : new EventRepetitionDtoBuilder()
                    .withCount(event.getEventRepetitionSchedule().getCount())
                    .withFrequency(event.getEventRepetitionSchedule().getFrequency())
                    .withId(event.getEventRepetitionSchedule().getId())
                    .withInterval(event.getEventRepetitionSchedule().getInterval())
                    .withOwningEventId(event.getEventRepetitionSchedule().getOwningEvent().getId())
                    .withStartDate(event.getEventRepetitionSchedule().getStartDate())
                    .build())
        .setCanEdit(
            event.getCreatedBy().getUsername().equals(requestingUser.getUsername())
                || familyService.verfiyMinimumRoleSecurity(
                    event.getCalendar().getFamily(), requestingUser, Role.ADULT))
        .withAssignees(
            event.getAssignees().stream()
                .filter(assignee -> userIds.isEmpty() || userIds.contains(assignee.getId()))
                .map(
                    assignee ->
                        new ColorDtoBuilder()
                            .withUserId(assignee.getId())
                            .withUser(assignee.getFullname())
                            .withColor(colors.get(assignee.getId()))
                            .build())
                .collect(Collectors.toSet()))
        .build();
  }

  private CalendarEventDto buildEventDtoFromRecurringEvent(
      RecurringCalendarEvent recurringEvent,
      TimeZone timezone,
      User requestingUser,
      List<Long> userIds) {
    CalendarEvent event = recurringEvent.getOriginatingEvent();
    Map<Long, String> colors =
        event.getCalendar().getFamily().getMembers().stream()
            .collect(
                Collectors.toMap(
                    m -> m.getUser().getId(),
                    m ->
                        m.getEventColor() != null
                            ? m.getEventColor()
                            : m.getFamily().getEventColor()));
    return new CalendarEventDtoBuilder()
        .withCalendarId(event.getCalendar().getId())
        .setIsAllDay(event.isAllDay())
        .setIsFamilyEvent(event.isFamilyEvent())
        .withColor(event.isFamilyEvent() ? event.getCalendar().getFamily().getEventColor() : null)
        .withCreatedDate(event.getCreatedDatetime())
        .withCreator(UserDto.fromUserObj(event.getCreatedBy()))
        .withEndDate(
            DateUtil.toTimezone(
                recurringEvent.getEndDatetime(),
                TimeZone.getTimeZone(event.getTimezone()),
                timezone))
        .withStartDate(
            DateUtil.toTimezone(
                recurringEvent.getStartDatetime(),
                TimeZone.getTimeZone(event.getTimezone()),
                timezone))
        .withNotes(event.getNotes())
        .withDescription(event.getDescription())
        .setRecurringEvent(true)
        .withRecurringId(recurringEvent.getId())
        .withRepetitionSchedule(
            new EventRepetitionDtoBuilder()
                .withCount(event.getEventRepetitionSchedule().getCount())
                .withFrequency(event.getEventRepetitionSchedule().getFrequency())
                .withId(event.getEventRepetitionSchedule().getId())
                .withInterval(event.getEventRepetitionSchedule().getInterval())
                .withOwningEventId(event.getEventRepetitionSchedule().getOwningEvent().getId())
                .withStartDate(event.getEventRepetitionSchedule().getStartDate())
                .build())
        .setCanEdit(
            event.getCreatedBy().getUsername().equals(requestingUser.getUsername())
                || familyService.verfiyMinimumRoleSecurity(
                    event.getCalendar().getFamily(), requestingUser, Role.ADULT))
        .withAssignees(
            event.getAssignees().stream()
                .filter(assignee -> userIds.isEmpty() || userIds.contains(assignee.getId()))
                .map(
                    assignee ->
                        new ColorDtoBuilder()
                            .withUserId(assignee.getId())
                            .withUser(assignee.getFullname())
                            .withColor(colors.get(assignee.getId()))
                            .build())
                .collect(Collectors.toSet()))
        .build();
  }

  private List<RecurringCalendarEvent> addRepeatingEvents(
      EventRepetitionSchedule schedule, CalendarEvent parentEvent) {
    List<RecurringCalendarEvent> recurringEvents = new ArrayList<>();
    Date runningStartDate = java.sql.Date.from(parentEvent.getStartDatetime().toInstant());
    Date runningEndDate =
        parentEvent.getEndDatetime() != null
            ? java.sql.Date.from(parentEvent.getEndDatetime().toInstant())
            : null;
    java.util.Calendar cal = java.util.Calendar.getInstance();
    int field = java.util.Calendar.DAY_OF_YEAR;
    switch (schedule.getFrequency()) {
      case WEEKLY:
        field = java.util.Calendar.WEEK_OF_YEAR;
        break;
      case MONTHLY:
        field = java.util.Calendar.MONTH;
        break;
      case YEARLY:
        field = java.util.Calendar.YEAR;
        break;
      default:
        break;
    }
    // we start at 1 to account for the originating event being the first occurrence
    for (int i = 1; i < schedule.getCount(); i++) {
      cal.setTime(runningStartDate);
      cal.add(field, schedule.getInterval());
      runningStartDate = cal.getTime();
      if (runningEndDate != null) {
        cal.setTime(runningEndDate);
        cal.add(field, schedule.getInterval());
        runningEndDate = cal.getTime();
      }
      // if the request was to only add events during the week and this date falls on a weekend, we
      // will decrement
      // the counter to act like this loop execution didn't happen after updating the dates.
      if (schedule.getFrequency().equals(CalendarRepetitionFrequency.WEEK_DAY)
          && (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY
              || cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY)) {
        i = i - 1;
        continue;
      }
      RecurringCalendarEvent recurringEvent = new RecurringCalendarEvent();
      recurringEvent.setOriginatingEvent(parentEvent);
      recurringEvent.setStartDatetime(new Timestamp(runningStartDate.getTime()));
      recurringEvent.setEndDatetime(new Timestamp(runningEndDate.getTime()));
      RecurringCalendarEvent savedRecurringEvent = recurringEventRepository.save(recurringEvent);

      recurringEvents.add(savedRecurringEvent);
    }
    return recurringEvents;
  }

  private List<RecurringCalendarEvent> updateFutureEvents(
      EventRepetitionSchedule schedule, CalendarEvent parentEvent) {
    List<RecurringCalendarEvent> returnEvents = new ArrayList<>();
    List<RecurringCalendarEvent> events =
        recurringEventRepository.getOrderedEventsByOriginatingId(parentEvent.getId());
    Date runningStartDate = java.sql.Date.from(parentEvent.getStartDatetime().toInstant());
    Date runningEndDate =
        parentEvent.getEndDatetime() != null
            ? java.sql.Date.from(parentEvent.getEndDatetime().toInstant())
            : null;
    java.util.Calendar cal = java.util.Calendar.getInstance();
    int field = java.util.Calendar.DAY_OF_YEAR;
    switch (schedule.getFrequency()) {
      case WEEKLY:
        field = java.util.Calendar.WEEK_OF_YEAR;
        break;
      case MONTHLY:
        field = java.util.Calendar.MONTH;
        break;
      case YEARLY:
        field = java.util.Calendar.YEAR;
        break;
      default:
        break;
    }
    // we start at 1 to account for the originating event being the first occurrence
    for (int i = 1; i < schedule.getCount(); i++) {
      cal.setTime(runningStartDate);
      cal.add(field, schedule.getInterval());
      runningStartDate = cal.getTime();
      if (runningEndDate != null) {
        cal.setTime(runningEndDate);
        cal.add(field, schedule.getInterval());
        runningEndDate = cal.getTime();
      }
      // if the request was to only add events during the week and this date falls on a weekend, we
      // will decrement
      // the counter to act like this loop execution didn't happen after updating the dates.
      if (schedule.getFrequency().equals(CalendarRepetitionFrequency.WEEK_DAY)
          && (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SATURDAY
              || cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY)) {
        i = i - 1;
        continue;
      }
      if (i > events.size()) {
        RecurringCalendarEvent recurringEvent = new RecurringCalendarEvent();
        recurringEvent.setOriginatingEvent(parentEvent);
        recurringEvent.setStartDatetime(new Timestamp(runningStartDate.getTime()));
        recurringEvent.setEndDatetime(new Timestamp(runningEndDate.getTime()));
        returnEvents.add(recurringEventRepository.save(recurringEvent));
      } else {
        if (events.get(i).getStartDatetime().compareTo(Date.from(Instant.now())) == 1) {
          RecurringCalendarEvent event = events.get(i);
          event.setStartDatetime(new Timestamp(runningStartDate.getTime()));
          event.setEndDatetime(new Timestamp(runningEndDate.getTime()));
          returnEvents.add(recurringEventRepository.save(event));
        } else {
          returnEvents.add(events.get(i));
        }
      }
    }
    return returnEvents;
  }

  private CalendarEvent breakRecurringEvent(
      RecurringCalendarEvent recurringEvent, CalendarEventDto request) {
    recurringEventRepository.deleteById(recurringEvent.getId());
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(
            userService.getRequestingUser(),
            recurringEvent.getOriginatingEvent().getCalendar().getFamily());
    CalendarEvent event = new CalendarEvent();
    event.setCalendar(recurringEvent.getOriginatingEvent().getCalendar());
    event.setAllDay(request.isAllDay());
    event.setCreatedBy(recurringEvent.getOriginatingEvent().getCreatedBy());
    event.setCreatedDatetime(recurringEvent.getOriginatingEvent().getCreatedDatetime());
    event.setDescription(request.getDescription());
    event.setStartDatetime(DateUtil.parseTimestamp(request.getStartDate()));
    event.setEndDatetime(DateUtil.parseTimestamp(request.getEndDate()));
    event.setTimezone(timezone.getID());
    event.setFamilyEvent(request.isFamilyEvent());
    event.setNotes(request.getNotes());
    event.setAssignees(new HashSet<>(recurringEvent.getOriginatingEvent().getAssignees()));
    return eventRepository.save(event);
  }

  private String getEventColor(CalendarEvent event, Family family, User user) {
    String color =
        event.isFamilyEvent()
            ? family.getEventColor()
            : family.getMembers().stream()
                .filter(member -> member.getUser().getUsername().equals(user.getUsername()))
                .findFirst()
                .get()
                .getEventColor();
    return color;
  }

  private Map<CalendarField, List<SearchFilter>> getSearchFilters(List<Calendar> calendars) {
    Map<CalendarField, List<SearchFilter>> filters = new HashMap<>();
    Set<SearchFilter> calendarFilters = new HashSet<>();
    Set<SearchFilter> familyFilters = new HashSet<>();
    Set<SearchFilter> userFilters = new HashSet<>();

    for (Calendar calendar : calendars) {
      familyFilters.add(
          new SearchFilter(calendar.getFamily().getId(), calendar.getFamily().getName()));
      calendarFilters.add(
          new SearchFilter(
              calendar.getId(),
              calendar.getFamily().getName() + " - " + calendar.getDescription()));
      for (FamilyMembers member : calendar.getFamily().getMembers()) {
        userFilters.add(new SearchFilter(member.getUser().getId(), member.getUser().getFullname()));
      }
    }

    filters.put(CalendarField.CALENDAR, new ArrayList<>(calendarFilters));
    filters.put(CalendarField.FAMILY, new ArrayList<>(familyFilters));
    filters.put(CalendarField.USER, new ArrayList<>(userFilters));
    return filters;
  }

  private void verifyCalendarSearchRequest(CalendarSearchRequestDto request) {
    if (request.getStart() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Must specify a start date.");
    }
    if (request.getEnd() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Must specify an end date.");
    }
    if (request.getEnd().compareTo(request.getStart()) < 0) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE, "End date occurs before start date.");
    }
  }

  @Override
  public void addAssignee(CalendarEventDto request) {
    User requestingUser = userService.getRequestingUser();

    if (request.getId() != null) {
      Optional<CalendarEvent> event = eventRepository.findById(request.getId());
      if (event.isEmpty()) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.EVENT_DOESNT_EXIST,
            "Event with id " + request.getId() + " not found.");
      }
      if (!familyService.verfiyMinimumRoleSecurity(
          event.get().getCalendar().getFamily(), requestingUser, Role.CHILD)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }
      if (!canAddAssignee(event.get(), requestingUser)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }

      request
          .getAssignees()
          .forEach(
              assigneeDto -> {
                User assignee = userService.getUserById(assigneeDto.getUserId());
                event.get().getAssignees().add(assignee);
              });

      eventRepository.save(event.get());
    } else if (request.getRecurringId() != null) {
      Optional<RecurringCalendarEvent> recurringEvent =
          recurringEventRepository.findById(request.getRecurringId());
      if (recurringEvent.isEmpty()) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.EVENT_DOESNT_EXIST,
            "Recurring event with id " + request.getRecurringId() + " not found.");
      }
      CalendarEvent event;
      if (request.getDetachEvent()) {
        recurringEventRepository.deleteById(recurringEvent.get().getId());
        event = new CalendarEvent(recurringEvent.get().getOriginatingEvent());
      } else {
        event = recurringEvent.get().getOriginatingEvent();
      }

      if (!familyService.verfiyMinimumRoleSecurity(
          event.getCalendar().getFamily(), requestingUser, Role.CHILD)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }
      if (!canAddAssignee(event, requestingUser)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }

      request
          .getAssignees()
          .forEach(
              assigneeDto -> {
                User assignee = userService.getUserById(assigneeDto.getUserId());
                event.getAssignees().add(assignee);
              });

      eventRepository.save(event);
    } else {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Either an event id or a recurring id must be supplied.");
    }
  }

  @Override
  public void removeAssignee(CalendarEventDto request) {
    User requestingUser = userService.getRequestingUser();

    if (request.getId() != null) {
      Optional<CalendarEvent> event = eventRepository.findById(request.getId());
      if (event.isEmpty()) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.EVENT_DOESNT_EXIST,
            "Event with id " + request.getId() + " not found.");
      }

      if (!familyService.verfiyMinimumRoleSecurity(
          event.get().getCalendar().getFamily(), requestingUser, Role.CHILD)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }
      if (!canAddAssignee(event.get(), requestingUser)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }

      request
          .getAssignees()
          .forEach(
              assigneeDto -> {
                User assignee = userService.getUserById(assigneeDto.getUserId());

                event.get().getAssignees().remove(assignee);
              });

      eventRepository.save(event.get());
    } else if (request.getRecurringId() != null) {
      Optional<RecurringCalendarEvent> recurringEvent =
          recurringEventRepository.findById(request.getRecurringId());
      if (recurringEvent.isEmpty()) {
        throw new ResourceNotFoundException(
            ApiExceptionCode.EVENT_DOESNT_EXIST,
            "Recurring event with id " + request.getRecurringId() + " not found.");
      }
      CalendarEvent event;
      if (request.getDetachEvent()) {
        recurringEventRepository.deleteById(recurringEvent.get().getId());
        event = new CalendarEvent(recurringEvent.get().getOriginatingEvent());
      } else {
        event = recurringEvent.get().getOriginatingEvent();
      }

      if (!familyService.verfiyMinimumRoleSecurity(
          event.getCalendar().getFamily(), requestingUser, Role.CHILD)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }
      if (!canAddAssignee(event, requestingUser)) {
        throw new AuthorizationException(
            ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
            "User not authorized to complete this action.");
      }

      request
          .getAssignees()
          .forEach(
              assigneeDto -> {
                User assignee = userService.getUserById(assigneeDto.getUserId());
                event.getAssignees().remove(assignee);
              });

      eventRepository.save(event);
    } else {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Either an event id or a recurring id must be supplied.");
    }
  }

  /**
   * Checks to see if a user can be added to an event by the requesting user. The user may be added
   * as long as the event is not a family event. (no users can be added if it is a family event) and
   * they meet one of the following criteria
   *
   * <ul>
   *   <li>1. the requesting user is at least an admin
   *   <li>2. the requesting user created the event
   *   <li>3. the requesting user is already assigned to the event
   * </ul>
   *
   * @param event
   * @param requestUser
   * @return
   */
  private boolean canAddAssignee(CalendarEvent event, User requestUser) {
    if (event.isFamilyEvent()) {
      return false;
    }

    if (familyService.verfiyMinimumRoleSecurity(
        event.getCalendar().getFamily(), requestUser, Role.ADMIN)) {
      return true;
    }
    if (event.getCreatedBy().getId() == requestUser.getId()) {
      return true;
    }
    if (event.getAssignees().contains(requestUser)) {
      return true;
    }

    return false;
  }
}
