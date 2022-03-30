package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.Date;
import com.familyorg.familyorganizationapp.DTO.EventRepetitionDto;
import com.familyorg.familyorganizationapp.domain.CalendarRepetitionFrequency;

public class EventRepetitionDtoBuilder implements DtoBuilder<EventRepetitionDto> {
  private Long id;
  private CalendarRepetitionFrequency frequency;
  private Integer interval;
  private Integer count;
  private Date startDate;
  private Long owningEventId;

  public EventRepetitionDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public EventRepetitionDtoBuilder withFrequency(CalendarRepetitionFrequency frequency) {
    this.frequency = frequency;
    return this;
  }

  public EventRepetitionDtoBuilder withInterval(Integer interval) {
    this.interval = interval;
    return this;
  }

  public EventRepetitionDtoBuilder withCount(Integer count) {
    this.count = count;
    return this;
  }

  public EventRepetitionDtoBuilder withStartDate(Date startDate) {
    this.startDate = startDate;
    return this;
  }

  public EventRepetitionDtoBuilder withOwningEventId(Long id) {
    this.owningEventId = id;
    return this;
  }

  @Override
  public EventRepetitionDto build() {
    return new EventRepetitionDto(id, frequency, interval, count, startDate, owningEventId);
  }
}
