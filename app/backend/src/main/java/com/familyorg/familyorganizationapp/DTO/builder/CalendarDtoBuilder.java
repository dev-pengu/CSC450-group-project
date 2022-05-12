package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.LinkedList;
import java.util.List;
import com.familyorg.familyorganizationapp.DTO.CalendarDto;
import com.familyorg.familyorganizationapp.DTO.CalendarEventDto;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.domain.Family;

public class CalendarDtoBuilder implements DtoBuilder<CalendarDto> {
  private Long id;
  private String description;
  private Boolean isDefault = false;
  private Long familyId;
  private List<CalendarEventDto> events;
  private String color;

  public CalendarDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public CalendarDtoBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public CalendarDtoBuilder setDefault(Boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  public CalendarDtoBuilder withFamily(Long id) {
    this.familyId = id;
    return this;
  }

  public CalendarDtoBuilder withFamily(Family family) {
    this.familyId = family.getId();
    return this;
  }

  public CalendarDtoBuilder withFamily(FamilyDto family) {
    this.familyId = family.getId();
    return this;
  }

  public CalendarDtoBuilder withEvents(List<CalendarEventDto> events) {
    this.events = events;
    return this;
  }

  public CalendarDtoBuilder addEvent(CalendarEventDto event) {
    if (events == null) {
      this.events = new LinkedList<>();
    }
    this.events.add(event);
    return this;
  }

  public CalendarDtoBuilder withColor(String color) {
    this.color = color;
    return this;
  }

  @Override
  public CalendarDto build() {
    return new CalendarDto(id, description, isDefault, familyId, events, color);
  }
}
