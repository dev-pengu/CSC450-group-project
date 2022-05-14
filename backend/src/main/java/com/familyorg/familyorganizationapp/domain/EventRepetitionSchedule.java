package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import com.familyorg.familyorganizationapp.DTO.EventRepetitionDto;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "event_repetition")
public class EventRepetitionSchedule implements Serializable {

  private static final long serialVersionUID = -9209969685666806158L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_repetition_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "frequency", columnDefinition = "VARCHAR(7)")
  private CalendarRepetitionFrequency frequency;

  @Column(name = "_interval", columnDefinition = "INT")
  private Integer interval;

  @Column(name = "count", columnDefinition = "INT")
  private Integer count;

  @Column(name = "start_date", columnDefinition = "DATE")
  private Date startDate;

  @JsonIgnore
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "calendar_event_id", referencedColumnName = "calendar_event_id",
      columnDefinition = "BIGINT")
  private CalendarEvent owningEvent;

  public EventRepetitionSchedule() {}

  public EventRepetitionSchedule(Long id, CalendarRepetitionFrequency frequency, Integer interval,
      Integer count, Date startDate, CalendarEvent owningEvent) {
    super();
    this.id = id;
    this.frequency = frequency;
    this.interval = interval;
    this.count = count;
    this.startDate = startDate;
    this.owningEvent = owningEvent;
  }

  public EventRepetitionSchedule(CalendarRepetitionFrequency frequency, Integer interval,
      Integer count, Date startDate, CalendarEvent owningEvent) {
    super();
    this.frequency = frequency;
    this.interval = interval;
    this.count = count;
    this.startDate = startDate;
    this.owningEvent = owningEvent;
  }

  public EventRepetitionSchedule(CalendarEvent parentEvent, EventRepetitionDto repetitionSchedule) {
    this.frequency = repetitionSchedule.getFrequency();
    this.count = repetitionSchedule.getCount();
    this.interval = repetitionSchedule.getInterval();
    this.owningEvent = parentEvent;
    Date startDate = new Date(parentEvent.getStartDatetime().getTime());
    this.startDate = startDate;
    this.id = repetitionSchedule.getId();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CalendarRepetitionFrequency getFrequency() {
    return frequency;
  }

  public void setFrequency(CalendarRepetitionFrequency frequency) {
    this.frequency = frequency;
  }

  public Integer getInterval() {
    return interval;
  }

  public void setInterval(Integer interval) {
    this.interval = interval;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public CalendarEvent getOwningEvent() {
    return owningEvent;
  }

  public void setOwningEvent(CalendarEvent owningEvent) {
    this.owningEvent = owningEvent;
  }

  public boolean dataFieldsEquals(EventRepetitionSchedule other) {
    if (this == other)
      return true;
    return Objects.equals(count, other.count) && frequency == other.frequency
        && Objects.equals(interval, other.interval) && Objects.equals(startDate, other.startDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, frequency, id, interval, startDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EventRepetitionSchedule other = (EventRepetitionSchedule) obj;
    return Objects.equals(count, other.count) && frequency == other.frequency
        && Objects.equals(id, other.id) && Objects.equals(interval, other.interval)
        && Objects.equals(startDate, other.startDate);
  }

  @Override
  public String toString() {
    return "EventRepetitionSchedule [id=" + id + ", frequency=" + frequency + ", interval="
        + interval + ", count=" + count + ", startDate=" + startDate + "]";
  }


}
