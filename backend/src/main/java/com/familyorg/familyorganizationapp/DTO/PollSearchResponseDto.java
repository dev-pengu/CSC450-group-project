package com.familyorg.familyorganizationapp.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.familyorg.familyorganizationapp.domain.search.PollField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;

public class PollSearchResponseDto {
  private Boolean closed;
  private Boolean unVoted;
  private Date start;
  private Date end;
  private Map<PollField, List<SearchFilter>> searchFilters;
  private Map<PollField, List<SearchFilter>> activeSearchFilters;
  private List<PollDto> polls;

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

  public Map<PollField, List<SearchFilter>> getSearchFilters() {
    return searchFilters;
  }

  public void setSearchFilters(Map<PollField, List<SearchFilter>> searchFilters) {
    this.searchFilters = searchFilters;
  }

  public Map<PollField, List<SearchFilter>> getActiveSearchFilters() {
    return activeSearchFilters;
  }

  public void setActiveSearchFilters(Map<PollField, List<SearchFilter>> activeSearchFilters) {
    this.activeSearchFilters = activeSearchFilters;
  }

  public List<PollDto> getPolls() {
    return polls;
  }

  public void setPolls(List<PollDto> polls) {
    this.polls = polls;
  }

  public void addSearchFilter(PollField key, SearchFilter filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).add(filter);
  }

  public void addAllSearchFilters(PollField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.putIfAbsent(key, new ArrayList<>());
    this.searchFilters.get(key).addAll(filter);
  }

  public void setSearchFiltersByField(PollField key, List<SearchFilter> filter) {
    if (this.searchFilters == null) {
      this.searchFilters = new HashMap<>();
    }
    this.searchFilters.put(key, filter);
  }

  public void addActiveFilter(PollField key, SearchFilter filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).add(filter);
  }

  public void addAllActiveFilters(PollField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.putIfAbsent(key, new ArrayList<>());
    this.activeSearchFilters.get(key).addAll(filter);
  }

  public void setActiveFiltersByField(PollField key, List<SearchFilter> filter) {
    if (this.activeSearchFilters == null) {
      this.activeSearchFilters = new HashMap<>();
    }
    this.activeSearchFilters.put(key, filter);
  }

  @Override
  public int hashCode() {
    return Objects.hash(activeSearchFilters, closed, end, polls, searchFilters, start, unVoted);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PollSearchResponseDto other = (PollSearchResponseDto) obj;
    return Objects.equals(activeSearchFilters, other.activeSearchFilters)
        && Objects.equals(closed, other.closed) && Objects.equals(end, other.end)
        && Objects.equals(polls, other.polls) && Objects.equals(searchFilters, other.searchFilters)
        && Objects.equals(start, other.start) && Objects.equals(unVoted, other.unVoted);
  }

  @Override
  public String toString() {
    return "PollSearchResponseDto [closed=" + closed + ", unVoted=" + unVoted + ", start=" + start
        + ", end=" + end + ", searchFilters=" + searchFilters + ", activeSearchFilters="
        + activeSearchFilters + ", polls=" + polls + "]";
  }

}
