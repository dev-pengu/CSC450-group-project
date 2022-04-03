package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.domain.QCalendarEvent;
import com.familyorg.familyorganizationapp.repository.custom.CalendarEventRepositoryCustom;

@Repository
public class CalendarEventRepositoryImpl extends QuerydslRepositorySupport
    implements CalendarEventRepositoryCustom {

  private QCalendarEvent eventTable = QCalendarEvent.calendarEvent;

  public CalendarEventRepositoryImpl() {
    super(CalendarEvent.class);
  }

  @Override
  public Map<Long, List<CalendarEvent>> getEventsByCalendarIdsInDateRange(Set<Long> calendarIds,
      Timestamp start, Timestamp end) {
    Map<Long, List<CalendarEvent>> result = new HashMap<>();
    List<CalendarEvent> events =
        from(eventTable)
            .where(eventTable.calendar.id.in(calendarIds)
                .and(eventTable.startDatetime.goe(start).and(eventTable.endDatetime.loe(end))))
            .fetch();

    events.forEach(event -> {
      if (result.containsKey(event.getCalendar().getId())) {
        result.get(event.getCalendar().getId()).add(event);
      } else {
        result.put(event.getCalendar().getId(), new ArrayList<CalendarEvent>() {
          {
            add(event);
          }
        });
      }
    });
    return result;
  }


}
