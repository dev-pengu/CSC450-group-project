package com.familyorg.familyorganizationapp.DTO;

import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ToDoField;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ToDoListSearchRequestDto {
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
  private Date start;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm")
  private Date end;
  private Boolean completed;
  private Map<ToDoField, List<SearchFilter>> filters = new HashMap<>();

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

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Map<ToDoField, List<SearchFilter>> getFilters() {
    return filters;
  }

  public void setFilters(Map<ToDoField, List<SearchFilter>> filters) {
    this.filters = filters;
  }

  public void addFilter(ToDoField key, SearchFilter filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).add(filter);
  }

  public void addAllFilters(ToDoField key, List<SearchFilter> filterList) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).addAll(filterList);
  }

  public void setFiltersByField(ToDoField key, List<SearchFilter> filterList) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.put(key, filterList);
  }

  public List<SearchFilter> getFiltersByField(ToDoField field) {
    return this.filters.getOrDefault(field, Collections.emptyList());
  }

  public List<Long> getIdsByField(ToDoField field) {
    return this.filters.getOrDefault(field, Collections.emptyList()).stream()
        .map(SearchFilter::getId)
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "ToDoListSearchRequestDto{"
        + "start="
        + start
        + ", end="
        + end
        + ", completed="
        + completed
        + ", filters="
        + filters
        + '}';
  }
}
