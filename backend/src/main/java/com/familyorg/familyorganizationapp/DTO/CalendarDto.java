package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Objects;

public class CalendarDto {
  private Long id;
  private String description;
  private Boolean defaultCal;
  private Long familyId;
  @JsonInclude(Include.NON_EMPTY)
  private List<CalendarEventDto> events;
  @JsonInclude(Include.NON_NULL)
  private String color;

  public CalendarDto(Long id, String description, Boolean isDefault, Long familyId,
      List<CalendarEventDto> events, String color) {
    super();
    this.id = id;
    this.description = description;
    this.defaultCal = isDefault;
    this.familyId = familyId;
    this.events = events;
    this.color = color;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public Boolean isDefault() {
    return defaultCal;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public List<CalendarEventDto> getEvents() {
    return events;
  }

  public String getColor() {
    return color;
  }


  @Override
  public int hashCode() {
    return Objects.hash(events, familyId, id, defaultCal, description, color);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CalendarDto other = (CalendarDto) obj;
    return Objects.equals(events, other.events)
        && Objects.equals(familyId, other.familyId) && Objects.equals(id, other.id)
        && Objects.equals(defaultCal, other.defaultCal)
        && Objects.equals(description, other.description)
        && Objects.equals(color, other.color);
  }

  @Override
  public String toString() {
    return "CalendarDto [id=" + id + ", description=" + description + ", isDefault=" + defaultCal
        + ", familyId=" + familyId + ", events=" + events + ", color=" + color + "]";
  }
}
