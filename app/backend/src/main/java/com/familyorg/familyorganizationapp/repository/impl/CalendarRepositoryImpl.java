package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.QCalendar;
import com.familyorg.familyorganizationapp.repository.custom.CalendarRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class CalendarRepositoryImpl extends QuerydslRepositorySupport
    implements CalendarRepositoryCustom {

  private QCalendar calendarTable = QCalendar.calendar;

  public CalendarRepositoryImpl() {
    super(Calendar.class);
  }

  @Override
  public List<Calendar> calendarDataByFamilyIds(List<Long> familyIds) {
    List<Calendar> calendars =
        from(calendarTable).where(calendarTable.family.id.in(familyIds)).fetch();
    return calendars;
  }

  @Override
  public List<Calendar> search(List<Long> familyIds, List<Long> calendarIds) {
    JPQLQuery<Calendar> query = from(calendarTable).where(calendarTable.family.id.in(familyIds));

    if (calendarIds != null && !calendarIds.isEmpty()) {
      query.where(calendarTable.id.in(calendarIds));
    }
    return query.distinct().fetch();
  }
}
