package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "recurring_event")
public class RecurringCalendarEvent implements Serializable {

  private static final long serialVersionUID = 6425781653505476012L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recurring_event_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "calendar_event_id",
      referencedColumnName = "calendar_event_id",
      columnDefinition = "BIGINT",
      nullable = false)
  private CalendarEvent originatingEvent;

  @Column(name = "start_datetime", columnDefinition = "TIMESTAMP", nullable = false)
  private Timestamp startDatetime;

  @Column(name = "end_datetime", columnDefinition = "TIMESTAMP", nullable = false)
  private Timestamp endDatetime;

  public RecurringCalendarEvent() {}

  public RecurringCalendarEvent(
      Long id, CalendarEvent originatingEvent, Timestamp startDatetime, Timestamp endDatetime) {
    super();
    this.id = id;
    this.originatingEvent = originatingEvent;
    this.startDatetime = startDatetime;
    this.endDatetime = endDatetime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CalendarEvent getOriginatingEvent() {
    return originatingEvent;
  }

  public void setOriginatingEvent(CalendarEvent originatingEvent) {
    this.originatingEvent = originatingEvent;
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

  @Override
  public int hashCode() {
    return Objects.hash(endDatetime, id, startDatetime);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RecurringCalendarEvent other = (RecurringCalendarEvent) obj;
    return Objects.equals(endDatetime, other.endDatetime)
        && Objects.equals(id, other.id)
        && Objects.equals(startDatetime, other.startDatetime);
  }

  @Override
  public String toString() {
    return "RecurringCalendarEvent [id="
        + id
        + ", originatingEvent="
        + originatingEvent.getId()
        + ", startDatetime="
        + startDatetime
        + ", endDatetime="
        + endDatetime
        + "]";
  }
}
