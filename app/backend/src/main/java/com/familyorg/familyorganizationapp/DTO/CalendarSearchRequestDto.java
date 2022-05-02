package com.familyorg.familyorganizationapp.DTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.familyorg.familyorganizationapp.domain.search.CalendarField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CalendarSearchRequestDto {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
  private Date start;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
  private Date end;
  private Map<CalendarField, List<SearchFilter>> filters;

  public CalendarSearchRequestDto() {
    filters = new HashMap<>();
  }

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

  public Map<CalendarField, List<SearchFilter>> getFilters() {
    return filters;
  }

  public void setFilters(Map<CalendarField, List<SearchFilter>> filters) {
    this.filters = filters;
  }

  public void addFilter(CalendarField key, SearchFilter filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).add(filter);
  }

  public void addAllFilters(CalendarField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).addAll(filter);
  }

  public void setFiltersByField(CalendarField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.put(key, filter);
  }

  public List<SearchFilter> getFiltersByField(CalendarField field) {
    return this.filters.getOrDefault(field, Collections.emptyList());
  }

  public List<Long> getIdsByField(CalendarField field) {
    return this.filters.getOrDefault(field, Collections.emptyList())
        .stream()
        .map(SearchFilter::getId)
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "CalendarSearchRequestDto [start=" + start + ", end=" + end + ", filters=" + filters
        + "]";
  }


}
