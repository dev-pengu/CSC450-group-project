package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "calendar_event")
public class CalendarEvent implements Serializable {

  private static final long serialVersionUID = -4660957146264858910L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "calendar_event_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "is_all_day", columnDefinition = "BOOLEAN")
  private Boolean allDay;

  @Column(name = "is_family_event", columnDefinition = "BOOLEAN")
  private Boolean familyEvent;

  @Column(name = "start_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp startDatetime;

  @Column(name = "end_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp endDatetime;

  @Column(name = "description", columnDefinition = "VARCHAR(256)")
  private String description;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @ManyToOne(fetch = FetchType.LAZY)
  private User createdBy;

  @Column(name = "created_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp createdDatetime;

  @Column(name = "timezone", columnDefinition = "VARCHAR(256)")
  private String timezone;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "event_repetition_id", referencedColumnName = "event_repetition_id",
      columnDefinition = "BIGINT")
  private EventRepetitionSchedule eventRepetitionSchedule;

  @OneToMany(mappedBy = "originatingEvent", fetch = FetchType.EAGER)
  private List<RecurringCalendarEvent> recurringEventInfo;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "calendar_id", referencedColumnName = "calendar_id",
      columnDefinition = "BIGINT")
  private Calendar calendar;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
  @JoinTable(
      name = "event_assignees",
      joinColumns = {@JoinColumn(name = "calendar_event_id")},
      inverseJoinColumns = {@JoinColumn(name = "user_id")})
  private Set<User> assignees = new HashSet<User>();

  public CalendarEvent() {
    super();
  }

  public CalendarEvent(Long id, Boolean allDay, Boolean familyEvent, Timestamp startDatetime,
      Timestamp endDatetime, String description, String notes, User createdBy,
      Timestamp createdDatetime, EventRepetitionSchedule eventRepetitionSchedule,
      List<RecurringCalendarEvent> recurringEventInfo, Calendar calendar, String timezone) {
    super();
    this.id = id;
    this.allDay = allDay;
    this.familyEvent = familyEvent;
    this.startDatetime = startDatetime;
    this.endDatetime = endDatetime;
    this.description = description;
    this.notes = notes;
    this.createdBy = createdBy;
    this.createdDatetime = createdDatetime;
    this.eventRepetitionSchedule = eventRepetitionSchedule;
    this.recurringEventInfo = recurringEventInfo;
    this.calendar = calendar;
    this.timezone = timezone;
  }

  public CalendarEvent(CalendarEvent other) {
    this.allDay = other.allDay;
    this.familyEvent = other.familyEvent;
    this.startDatetime = other.startDatetime;
    this.endDatetime = other.endDatetime;
    this.description = other.description;
    this.notes = other.notes;
    this.createdBy = other.createdBy;
    this.createdDatetime = other.createdDatetime;
    this.eventRepetitionSchedule = other.eventRepetitionSchedule;
    this.recurringEventInfo = other.recurringEventInfo;
    this.calendar = other.calendar;
    this.timezone = other.timezone;
    this.assignees = other.assignees;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Boolean isAllDay() {
    return allDay;
  }

  public void setAllDay(Boolean allDay) {
    this.allDay = allDay;
  }

  public Boolean isFamilyEvent() {
    return familyEvent;
  }

  public void setFamilyEvent(Boolean familyEvent) {
    this.familyEvent = familyEvent;
  }

  public Timestamp getStartDatetime() {
    return startDatetime;
  }

  public void setStartDatetime(Timestamp startDatetime) {
    this.startDatetime = startDatetime;
  }

  public Timestamp getEndDatetime() {
    return endDatetime;
  }

  public void setEndDatetime(Timestamp endDatetime) {
    this.endDatetime = endDatetime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public Timestamp getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Timestamp createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public EventRepetitionSchedule getEventRepetitionSchedule() {
    return eventRepetitionSchedule;
  }

  public void setEventRepetitionSchedule(EventRepetitionSchedule eventRepetitionSchedule) {
    this.eventRepetitionSchedule = eventRepetitionSchedule;
  }

  public List<RecurringCalendarEvent> getRecurringEventInfo() {
    return recurringEventInfo;
  }

  public void setRecurringEventInfo(List<RecurringCalendarEvent> recurringEventInfo) {
    this.recurringEventInfo = recurringEventInfo;
  }

  public Calendar getCalendar() {
    return calendar;
  }

  public void setCalendar(Calendar calendar) {
    this.calendar = calendar;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Set<User> getAssignees() {
    return assignees;
  }

  public void setAssignees(Set<User> assignees) {
    this.assignees = assignees;
  }

  @Override
  public int hashCode() {
    return Objects.hash(allDay, createdBy, createdDatetime, description, endDatetime,
        eventRepetitionSchedule, familyEvent, id, notes, recurringEventInfo, startDatetime,
        timezone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CalendarEvent other = (CalendarEvent) obj;
    return Objects.equals(allDay, other.allDay) && Objects.equals(createdBy, other.createdBy)
        && Objects.equals(createdDatetime, other.createdDatetime)
        && Objects.equals(description, other.description)
        && Objects.equals(endDatetime, other.endDatetime)
        && Objects.equals(eventRepetitionSchedule, other.eventRepetitionSchedule)
        && Objects.equals(familyEvent, other.familyEvent) && Objects.equals(id, other.id)
        && Objects.equals(notes, other.notes)
        && Objects.equals(recurringEventInfo, other.recurringEventInfo)
        && Objects.equals(startDatetime, other.startDatetime)
        && Objects.equals(timezone, other.timezone);
  }

  @Override
  public String toString() {
    return "CalendarEvent [id=" + id + ", allDay=" + allDay + ", familyEvent=" + familyEvent
        + ", startDatetime=" + startDatetime + ", endDatetime=" + endDatetime + ", description="
        + description + ", notes=" + notes + ", createdBy=" + createdBy + ", createdDatetime="
        + createdDatetime + ", eventRepetitionSchedule=" + eventRepetitionSchedule
        + ", recurringEventInfo=" + recurringEventInfo + ", timezone=" + timezone + "]";
  }
}
