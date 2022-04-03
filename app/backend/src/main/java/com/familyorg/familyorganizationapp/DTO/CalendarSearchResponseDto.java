package com.familyorg.familyorganizationapp.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.familyorg.familyorganizationapp.domain.search.CalendarField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;

public class CalendarSearchResponseDto {

  private Date start;
  private Date end;
  private Map<CalendarField, List<SearchFilter>> searchFilters;
  private Map<CalendarField, List<SearchFilter>> activeSearchFilters;
  private List<CalendarDto> calendars;

  public Date getStart() {
    return start;
  }

  public void setStart(Date start) {
    this.start = start;
  }

  public Date getEnd() {
    return end;
  }

  public void setEnd(Date end) {
    this.end = end;
  }

  public Map<CalendarField, List<SearchFilter>> getSearchFilters() {
    return searchFilters;
  }

  public void setSearchFilters(Map<CalendarField, List<SearchFilter>> searchFilters) {
    this.searchFilters = searchFilters;
  }

  public Map<CalendarField, List<SearchFilter>> getActiveSearchFilters() {
    return activeSearchFilters;
  }

  public void setActiveSearchFilters(Map<CalendarField, List<SearchFilter>> activeSearchFilters) {
    this.activeSearchFilters = activeSearchFilters;
  }

  public List<CalendarDto> getCalendars() {
    return calendars;
  }

  public void setCalendars(List<CalendarDto> calendars) {
    this.calendars = calendars;
  }

  public void addSearchFilter(CalendarField key, SearchFilter filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).add(filter);
  }

  public void addAllSearchFilters(CalendarField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).addAll(filter);
  }

  public void setSearchFiltersByField(CalendarField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.put(key, filter);
  }

  public void addActiveFilter(CalendarField key, SearchFilter filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).add(filter);
  }

  public void addAllActiveFilters(CalendarField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).addAll(filter);
  }

  public void setActiveFiltersByField(CalendarField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.put(key, filter);
  }

  @Override
  public String toString() {
    return "CalendarSearchResponseDto [start=" + start + ", end=" + end + ", searchFilters="
        + searchFilters + ", activeSearchFilters=" + activeSearchFilters + ", calendars="
        + calendars + "]";
  }


}
