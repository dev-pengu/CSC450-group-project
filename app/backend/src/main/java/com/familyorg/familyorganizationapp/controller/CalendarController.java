package com.familyorg.familyorganizationapp.controller;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.CalendarSearchResponseDto;
import com.familyorg.familyorganizationapp.service.CalendarService;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/calendar")
public class CalendarController {

  private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

  CalendarService calendarService;

  @Autowired
  public CalendarController(CalendarService calendarService) {
    this.calendarService = calendarService;
  }

  @PostMapping()
  public ResponseEntity<String> createCalendar(@RequestBody CalendarDto request) {
    calendarService.createCalendar(request);
    return new ResponseEntity<>("Calendar created successfully.", HttpStatus.CREATED);
  }

  @PostMapping("/event")
  public ResponseEntity<String> addEvent(@RequestBody CalendarEventDto request) {
    calendarService.addEvent(request);
    return new ResponseEntity<>("Event added to calendar succesfully.", HttpStatus.CREATED);
  }

  @PatchMapping()
  public ResponseEntity<String> updateCalendar(@RequestBody CalendarDto request) {
    calendarService.updateCalendar(request);
    return new ResponseEntity<>("Calendar updated successfully.", HttpStatus.OK);
  }

  @PatchMapping("/event")
  public ResponseEntity<String> updateEvent(@RequestBody CalendarEventDto request) {
    calendarService.updateEvent(request);
    return new ResponseEntity<>("Calendar event updated successfully.", HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteCalendar(@RequestParam Long id) {
    calendarService.deleteCalendar(id);
    return new ResponseEntity<>("Calendar deleted successfully.", HttpStatus.OK);
  }

  /**
   * Removes a calendar event. If isRecurring = true, will remove the occurrence of the repeating
   * action. If isRecurring = false, will remove the calendar event. By default, all recurring
   * events tied to the event to be deleted will be deleted unless removeRecurring is supplied with
   * a value of false. If removeRecurring = false, the first recurring event will be converted to a
   * calendar event.
   *
   * @param id
   * @param isRecurring
   * @param removeRecurring
   * @return Success message
   */
  @DeleteMapping("/event")
  public ResponseEntity<String> deleteEvent(@RequestParam Long id,
      @RequestParam("recurring") Boolean isRecurring,
      @RequestParam(name = "removeRecurring") Optional<Boolean> removeRecurring) {
    if (isRecurring) {
      calendarService.deleteRecurringEvent(id);
    } else {
      removeRecurring.ifPresentOrElse(value -> {
        calendarService.deleteEvent(id, value);
      }, () -> {
        calendarService.deleteEvent(id, true);
      });

    }

    return new ResponseEntity<>("Event deleted successfully.", HttpStatus.OK);
  }

  @PostMapping("/search")
  public ResponseEntity<CalendarSearchResponseDto> getCalendarWithEvents(
      @RequestBody CalendarSearchRequestDto request) {
    CalendarSearchResponseDto response = calendarService.search(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/event")
  public ResponseEntity<CalendarEventDto> getEvent(@RequestParam Long id,
      @RequestParam boolean isRecurring) {
    CalendarEventDto response = null;
    if (isRecurring) {
      response = calendarService.getRecurringEvent(id);
    } else {
      response = calendarService.getEvent(id);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/event/assign")
  public ResponseEntity<String> assignEvent(@RequestBody CalendarEventDto request) {
    calendarService.addAssignee(request);
    return new ResponseEntity<>("Assignee added successfully", HttpStatus.OK);
  }

  @PostMapping("/event/unassign")
  public ResponseEntity<String> unassignEvent(@RequestBody CalendarEventDto request) {
    calendarService.removeAssignee(request);
    return new ResponseEntity<>("Assignee removed succussfully", HttpStatus.OK);
  }
}
