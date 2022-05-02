package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.domain.QCalendarEvent;
import com.familyorg.familyorganizationapp.domain.QUser;
import com.familyorg.familyorganizationapp.repository.custom.CalendarEventRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class CalendarEventRepositoryImpl extends QuerydslRepositorySupport
    implements CalendarEventRepositoryCustom {

  private final Logger logger = LoggerFactory.getLogger(CalendarEventRepositoryImpl.class);

  private QCalendarEvent eventTable = QCalendarEvent.calendarEvent;
  private QUser userTable = QUser.user;

  public CalendarEventRepositoryImpl() {
    super(CalendarEvent.class);
  }

  @Override
  public Map<Long, List<CalendarEvent>> getEventsByCalendarIdsInDateRange(
      Set<Long> calendarIds, Timestamp start, Timestamp end, List<Long> userIds) {
    Map<Long, List<CalendarEvent>> result = new HashMap<>();
    JPQLQuery<CalendarEvent> query =
        from(eventTable)
            .leftJoin(eventTable.assignees, userTable)
            .where(eventTable.calendar.id.in(calendarIds));

    query.where(
        eventTable
            .startDatetime
            .goe(start)
            .and(eventTable.endDatetime.loe(end))
            .and(eventTable.startDatetime.eq(eventTable.endDatetime))
            .or(
                eventTable
                    .startDatetime
                    .goe(start)
                    .and(eventTable.startDatetime.ne(eventTable.endDatetime)))
            .or(
                eventTable
                    .endDatetime
                    .loe(end)
                    .and(eventTable.startDatetime.ne(eventTable.endDatetime)))
            .or(eventTable.startDatetime.loe(start).and(eventTable.endDatetime.goe(start))));
    if (!userIds.isEmpty()) {
      query.where(userTable.id.in(userIds).or(eventTable.familyEvent.eq(true)));
    }

    query.select(eventTable).distinct();

    List<CalendarEvent> events = query.fetch();

    events.forEach(
        event -> {
          if (result.containsKey(event.getCalendar().getId())) {
            result.get(event.getCalendar().getId()).add(event);
          } else {
            result.put(
                event.getCalendar().getId(),
                new ArrayList<CalendarEvent>() {
                  {
                    add(event);
                  }
                });
          }
        });
    return result;
  }
}
