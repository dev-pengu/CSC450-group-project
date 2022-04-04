package com.familyorg.familyorganizationapp.repository.custom;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;

public interface CalendarEventRepositoryCustom {
  Map<Long, List<CalendarEvent>> getEventsByCalendarIdsInDateRange(Set<Long> calendarIds,
      Timestamp start, Timestamp end, Set<Long> userIds);
}
