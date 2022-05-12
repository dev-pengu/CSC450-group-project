package com.familyorg.familyorganizationapp.DTO;

import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ShoppingListField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListSearchResponseDto {

  private Map<ShoppingListField, List<SearchFilter>> searchFilters;
  private Map<ShoppingListField, List<SearchFilter>> activeSearchFilters;
  private List<ShoppingListDto> lists;

  public ShoppingListSearchResponseDto() {
    searchFilters = new HashMap<>();
    activeSearchFilters = new HashMap<>();
    lists = new ArrayList<>();
  }

  public Map<ShoppingListField, List<SearchFilter>> getSearchFilters() {
    return searchFilters;
  }

  public void setSearchFilters(Map<ShoppingListField, List<SearchFilter>> searchFilters) {
    this.searchFilters = searchFilters;
  }

  public Map<ShoppingListField, List<SearchFilter>> getActiveSearchFilters() {
    return activeSearchFilters;
  }

  public void setActiveSearchFilters(
      Map<ShoppingListField, List<SearchFilter>> activeSearchFilters) {
    this.activeSearchFilters = activeSearchFilters;
  }

  public List<ShoppingListDto> getLists() {
    return lists;
  }

  public void setLists(List<ShoppingListDto> lists) {
    this.lists = lists;
  }

  public void addSearchFilter(ShoppingListField key, SearchFilter filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).add(filter);
  }

  public void addAllSearchFilters(ShoppingListField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).addAll(filter);
  }

  public void setSearchFiltersByField(ShoppingListField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.put(key, filter);
  }

  public void addActiveFilter(ShoppingListField key, SearchFilter filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).add(filter);
  }

  public void addAllActiveFilters(ShoppingListField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).addAll(filter);
  }

  public void setActiveFiltersByField(ShoppingListField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.put(key, filter);
  }

  @Override
  public String toString() {
    return "ShoppingListSearchResponseDto{"
        + "searchFilters="
        + searchFilters
        + ", activeSearchFilters="
        + activeSearchFilters
        + ", lists="
        + lists
        + '}';
  }
}
