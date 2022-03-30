package com.familyorg.familyorganizationapp.repository.custom;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;

public interface RecurringCalendarEventRepositoryCustom {
  List<RecurringCalendarEvent> getFutureEvents(Long originatingId);

  void removeFutureEvents(Long originatingId);

  List<RecurringCalendarEvent> getOrderedEventsByOriginatingId(Long originatingId);

  void removeRecurringByOriginatingId(Long originatingId);

  RecurringCalendarEvent getFirstOccurrence(Long originatingId);

  Map<Long, List<RecurringCalendarEvent>> getEventsByCalendarIdsInDateRange(Set<Long> calendarIds,
      Timestamp start, Timestamp end);

  void removeById(Long id);
}
