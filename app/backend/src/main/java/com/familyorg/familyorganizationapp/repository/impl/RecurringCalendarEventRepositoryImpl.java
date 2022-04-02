package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.QRecurringCalendarEvent;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.repository.custom.RecurringCalendarEventRepositoryCustom;
import com.querydsl.jpa.impl.JPADeleteClause;

@Repository
public class RecurringCalendarEventRepositoryImpl extends QuerydslRepositorySupport
    implements RecurringCalendarEventRepositoryCustom {

  private QRecurringCalendarEvent recurringEventTable =
      QRecurringCalendarEvent.recurringCalendarEvent;
  @Autowired
  private EntityManager entityManager;

  public RecurringCalendarEventRepositoryImpl() {
    super(RecurringCalendarEvent.class);
  }

  @Override
  public List<RecurringCalendarEvent> getFutureEvents(Long originatingId) {
    List<RecurringCalendarEvent> events = from(recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId)
            .and(recurringEventTable.startDatetime.goe(Timestamp.from(Instant.now()))))
        .orderBy(recurringEventTable.startDatetime.asc()).fetch();
    return events;
  }

  @Override
  public List<RecurringCalendarEvent> getOrderedEventsByOriginatingId(Long originatingId) {
    List<RecurringCalendarEvent> events =
        from(recurringEventTable).where(recurringEventTable.originatingEvent.id.eq(originatingId))
            .orderBy(recurringEventTable.startDatetime.asc()).fetch();
    return events;
  }

  @Override
  public void removeFutureEvents(Long originatingId) {
    new JPADeleteClause(entityManager, recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId)
            .and(recurringEventTable.startDatetime.goe(Timestamp.from(Instant.now()))))
        .execute();
  }

  @Override
  public void removeRecurringByOriginatingId(Long originatingId) {
    new JPADeleteClause(entityManager, recurringEventTable)
        .where(recurringEventTable.originatingEvent.id.eq(originatingId)).execute();
  }

  @Override
  public RecurringCalendarEvent getFirstOccurrence(Long originatingId) {
    RecurringCalendarEvent event =
        from(recurringEventTable).where(recurringEventTable.originatingEvent.id.eq(originatingId))
            .orderBy(recurringEventTable.startDatetime.asc()).fetchFirst();
    return event;
  }

  @Override
  public Map<Long, List<RecurringCalendarEvent>> getEventsByCalendarIdsInDateRange(
      Set<Long> calendarIds, Timestamp start, Timestamp end) {
    Map<Long, List<RecurringCalendarEvent>> result = new HashMap<>();
    List<RecurringCalendarEvent> events =
        from(recurringEventTable).where(recurringEventTable.originatingEvent.calendar.id
            .in(calendarIds).and(recurringEventTable.startDatetime.goe(start)
                .and(recurringEventTable.endDatetime.loe(end))))
            .fetch();
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
    new JPADeleteClause(entityManager, recurringEventTable).where(recurringEventTable.id.eq(id))
        .execute();

  }

}
