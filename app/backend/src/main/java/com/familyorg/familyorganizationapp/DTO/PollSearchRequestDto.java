package com.familyorg.familyorganizationapp.DTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import com.familyorg.familyorganizationapp.domain.search.PollField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;

public class PollSearchRequestDto {
  private Boolean closed;
  private Boolean unVoted;
  private Boolean limitToCreated;
  private Date start;
  private Date end;
  private Map<PollField, List<SearchFilter>> filters;

  public PollSearchRequestDto(Boolean closed, Boolean unVoted, Date start, Date end,
      Map<PollField, List<SearchFilter>> filters, Boolean limitToCreated) {
    super();
    this.closed = closed;
    this.unVoted = unVoted;
    this.start = start;
    this.end = end;
    this.filters = filters;
    this.limitToCreated = limitToCreated;
  }

  public Boolean getClosed() {
    return closed;
  }

  public void setClosed(Boolean closed) {
    this.closed = closed;
  }

  public Boolean getUnVoted() {
    return unVoted;
  }

  public void setUnVoted(Boolean unVoted) {
    this.unVoted = unVoted;
  }

  public Boolean shouldLimitToCreated() {
    if (limitToCreated == null) {
      return false;
    }
    return limitToCreated;
  }

  public void setLimitToCreated(Boolean limitToCreated) {
    this.limitToCreated = limitToCreated;
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

  public Map<PollField, List<SearchFilter>> getFilters() {
    return filters;
  }

  public void setFilters(Map<PollField, List<SearchFilter>> filters) {
    this.filters = filters;
  }

  public void addFilter(PollField key, SearchFilter filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).add(filter);
  }

  public void addAllFilters(PollField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.putIfAbsent(key, new ArrayList<>());
    this.filters.get(key).addAll(filter);
  }

  public void setFiltersByField(PollField key, List<SearchFilter> filter) {
    if (this.filters == null) {
      this.filters = new HashMap<>();
    }
    this.filters.put(key, filter);
  }

  public List<SearchFilter> getFiltersByField(PollField field) {
    return this.filters.getOrDefault(field, Collections.emptyList());
  }

  public List<Long> getIdsByField(PollField field) {
    return this.filters.getOrDefault(field, Collections.emptyList())
        .stream()
        .map(SearchFilter::getId)
        .collect(Collectors.toList());
  }

  @Override
  public int hashCode() {
    return Objects.hash(closed, end, filters, start, unVoted);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PollSearchRequestDto other = (PollSearchRequestDto) obj;
    return Objects.equals(closed, other.closed) && Objects.equals(end, other.end)
        && Objects.equals(filters, other.filters) && Objects.equals(start, other.start)
        && Objects.equals(unVoted, other.unVoted);
  }

  @Override
  public String toString() {
    return "PollSearchRequestDto [closed=" + closed + ", unVoted=" + unVoted + ", start=" + start
        + ", end=" + end + ", filters=" + filters + "]";
  }
}
