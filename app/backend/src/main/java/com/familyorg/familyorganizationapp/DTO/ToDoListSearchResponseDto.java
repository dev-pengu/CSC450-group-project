package com.familyorg.familyorganizationapp.DTO;

import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ToDoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToDoListSearchResponseDto {
  private Date start;
  private Date end;
  private Boolean completed;
  private Map<ToDoField, List<SearchFilter>> searchFilters;
  private Map<ToDoField, List<SearchFilter>> activeSearchFilters;
  private List<ToDoListDto> lists;

  public ToDoListSearchResponseDto() {
    searchFilters = new HashMap<>();
    activeSearchFilters = new HashMap<>();
    lists = new ArrayList<>();
  }

  public static ToDoListSearchResponseDto fromRequest(ToDoListSearchRequestDto request) {
    ToDoListSearchResponseDto response = new ToDoListSearchResponseDto();
    response.setStart(request.getStart());
    response.setEnd(request.getEnd());
    response.setCompleted(request.getCompleted());
    response.setActiveSearchFilters(request.getFilters());
    return response;
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

  public Boolean getCompleted() {
    return completed;
  }

  public void setCompleted(Boolean completed) {
    this.completed = completed;
  }

  public Map<ToDoField, List<SearchFilter>> getSearchFilters() {
    return searchFilters;
  }

  public void setSearchFilters(Map<ToDoField, List<SearchFilter>> searchFilters) {
    this.searchFilters = searchFilters;
  }

  public Map<ToDoField, List<SearchFilter>> getActiveSearchFilters() {
    return activeSearchFilters;
  }

  public void setActiveSearchFilters(Map<ToDoField, List<SearchFilter>> activeSearchFilters) {
    this.activeSearchFilters = activeSearchFilters;
  }

  public List<ToDoListDto> getLists() {
    return lists;
  }

  public void setLists(List<ToDoListDto> lists) {
    this.lists = lists;
  }

  public void addSearchFilter(ToDoField key, SearchFilter filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).add(filter);
  }

  public void addAllSearchFilters(ToDoField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).addAll(filter);
  }

  public void setSearchFiltersByField(ToDoField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.put(key, filter);
  }

  public void addActiveFilter(ToDoField key, SearchFilter filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).add(filter);
  }

  public void addAllActiveFilters(ToDoField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).addAll(filter);
  }

  public void setActiveFiltersByField(ToDoField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.put(key, filter);
  }

  @Override
  public String toString() {
    return "ToDoListSearchResponseDto{"
        + "start="
        + start
        + ", end="
        + end
        + ", completed="
        + completed
        + ", searchFilters="
        + searchFilters
        + ", activeSearchFilters="
        + activeSearchFilters
        + ", lists="
        + lists
        + '}';
  }
}
