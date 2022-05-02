package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.QCalendarEvent;
import com.familyorg.familyorganizationapp.domain.QRecurringCalendarEvent;
import com.familyorg.familyorganizationapp.domain.QUser;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.repository.custom.RecurringCalendarEventRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPADeleteClause;

@Repository
public class RecurringCalendarEventRepositoryImpl extends QuerydslRepositorySupport
    implements RecurringCalendarEventRepositoryCustom {

  private QRecurringCalendarEvent recurringEventTable =
      QRecurringCalendarEvent.recurringCalendarEvent;
  private QCalendarEvent eventTable = QCalendarEvent.calendarEvent;
  private QUser userTable = QUser.user;


  public RecurringCalendarEventRepositoryImpl() {
    super(RecurringCalendarEvent.class);
  }

  @Override
  public List<RecurringCalendarEvent> getFutureEvents(Long originatingId) {
    List<RecurringCalendarEvent> events = from(recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId)
            .and(recurringEventTable.startDatetime.goe(Timestamp.from(Instant.now()))))
        .orderBy(recurringEventTable.startDatetime.asc())
        .fetch();
    return events;
  }

  @Override
  public List<RecurringCalendarEvent> getOrderedEventsByOriginatingId(Long originatingId) {
    List<RecurringCalendarEvent> events =
        from(recurringEventTable).where(recurringEventTable.originatingEvent.id.eq(originatingId))
            .orderBy(recurringEventTable.startDatetime.asc())
            .fetch();
    return events;
  }

  @Override
  public void removeFutureEvents(Long originatingId) {
    new JPADeleteClause(getEntityManager(), recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId)
            .and(recurringEventTable.startDatetime.goe(Timestamp.from(Instant.now()))))
        .execute();
  }

  @Override
  public void removeRecurringByOriginatingId(Long originatingId) {
    new JPADeleteClause(getEntityManager(), recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId))
        .execute();
  }

  @Override
  public RecurringCalendarEvent getFirstOccurrence(Long originatingId) {
    RecurringCalendarEvent event =
        from(recurringEventTable).where(recurringEventTable.originatingEvent.id.eq(originatingId))
            .orderBy(recurringEventTable.startDatetime.asc())
            .fetchFirst();
    return event;
  }

  @Override
  public Map<Long, List<RecurringCalendarEvent>> getEventsByCalendarIdsInDateRange(
      Set<Long> calendarIds, Timestamp start, Timestamp end, List<Long> userIds) {
    Map<Long, List<RecurringCalendarEvent>> result = new HashMap<>();

    JPQLQuery<RecurringCalendarEvent> query =
        from(recurringEventTable)
            .innerJoin(recurringEventTable.originatingEvent, eventTable)
            .where(recurringEventTable.originatingEvent.calendar.id
                .in(calendarIds)
                .and(recurringEventTable.startDatetime.goe(start)
                    .and(recurringEventTable.endDatetime.loe(end))));

    if (userIds != null && !userIds.isEmpty()) {
      query.leftJoin(eventTable.assignees, userTable);
      query.where(userTable.id.in(userIds).or(eventTable.familyEvent.eq(true)));
    }

    query.select(recurringEventTable).distinct();

    List<RecurringCalendarEvent> events = query.fetch();
    events.forEach(event -> {
      if (result.containsKey(event.getOriginatingEvent().getCalendar().getId())) {
        result.get(event.getOriginatingEvent().getCalendar().getId()).add(event);
      } else {
        result.put(event.getOriginatingEvent().getCalendar().getId(),
            new ArrayList<RecurringCalendarEvent>() {
              {
                add(event);
              }
            });
      }
    });
    return result;
  }

  @Override
  public void removeById(Long id) {
    new JPADeleteClause(getEntityManager(), recurringEventTable)
        .where(recurringEventTable.id.eq(id))
        .execute();

  }

}
