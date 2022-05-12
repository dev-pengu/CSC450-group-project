package com.familyorg.familyorganizationapp.DTO;

import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.domain.search.ShoppingListField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShoppingListSearchRequestDto {
  private Map<ShoppingListField, List<SearchFilter>> filters;

  public Map<ShoppingListField, List<SearchFilter>> getFilters() {
    return filters;
  }

  public void setFilters(Map<ShoppingListField, List<SearchFilter>> filters) {
    this.filters = filters;
  }

  public void addFilter(ShoppingListField key, SearchFilter filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).add(filter);
  }

  public void addAllFilters(ShoppingListField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).addAll(filter);
  }

  public void setFiltersByField(ShoppingListField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.put(key, filter);
  }

  public List<SearchFilter> getFiltersByField(ShoppingListField field) {
    return this.filters.getOrDefault(field, Collections.emptyList());
  }

  public List<Long> getIdsByField(ShoppingListField field) {
    return this.filters.getOrDefault(field, Collections.emptyList()).stream()
        .map(SearchFilter::getId)
        .collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "ShoppingListSearchRequestDto{" + "filters=" + filters + '}';
  }
}
