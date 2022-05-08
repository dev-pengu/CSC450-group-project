package com.familyorg.familyorganizationapp.DTO;

import java.util.Date;
import java.util.Objects;
import com.familyorg.familyorganizationapp.domain.CalendarRepetitionFrequency;

public class EventRepetitionDto {
  private final Long id;
  private final CalendarRepetitionFrequency frequency;
  private final Integer interval;
  private final Integer count;
  private final Date startDate;
  private final Long owningEventId;

  public EventRepetitionDto(Long id, CalendarRepetitionFrequency frequency, Integer interval,
      Integer count, Date startDate, Long owningEventId) {
    super();
    this.id = id;
    this.frequency = frequency;
    this.interval = interval;
    this.count = count;
    this.startDate = startDate;
    this.owningEventId = owningEventId;
  }

  public Long getId() {
    return id;
  }

  public CalendarRepetitionFrequency getFrequency() {
    return frequency;
  }

  public Integer getInterval() {
    return interval;
  }

  public Integer getCount() {
    return count;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Long getOwningEventId() {
    return owningEventId;
  }

  @Override
  public String toString() {
    return "EventRepetitionDto [id=" + id + ", frequency=" + frequency + ", interval=" + interval
        + ", count=" + count + ", startDate=" + startDate + ", owningEventId=" + owningEventId
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, frequency, id, interval, startDate, owningEventId);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    EventRepetitionDto other = (EventRepetitionDto) obj;
    return Objects.equals(count, other.count) && frequency == other.frequency
        && Objects.equals(id, other.id) && Objects.equals(interval, other.interval)
        && Objects.equals(startDate, other.startDate)
        && Objects.equals(owningEventId, other.owningEventId);
  }


}
