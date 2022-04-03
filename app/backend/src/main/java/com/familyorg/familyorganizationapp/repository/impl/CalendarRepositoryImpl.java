package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.QCalendar;
import com.familyorg.familyorganizationapp.domain.QFamily;
import com.familyorg.familyorganizationapp.domain.QFamilyMembers;
import com.familyorg.familyorganizationapp.repository.custom.CalendarRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class CalendarRepositoryImpl extends QuerydslRepositorySupport
    implements CalendarRepositoryCustom {
  private Logger logger = LoggerFactory.getLogger(CalendarRepositoryImpl.class);

  private QCalendar calendarTable = QCalendar.calendar;
  private QFamily familyTable = QFamily.family;
  private QFamilyMembers memberTable = QFamilyMembers.familyMembers;

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
  public List<Calendar> search(List<Long> familyIds, List<Long> calendarIds, List<Long> userIds) {
    JPQLQuery<Calendar> query = from(calendarTable).innerJoin(calendarTable.family, familyTable)
        .innerJoin(familyTable.members, memberTable)
        .select(calendarTable)
        .distinct();
    if (familyIds != null && familyIds.size() > 0) {
      query.where(familyTable.id.in(familyIds));
    }
    if (calendarIds != null && calendarIds.size() > 0) {
      query.where(calendarTable.id.in(calendarIds));
    }
    if (userIds != null && userIds.size() > 0) {
      query.where(memberTable.user.id.in(userIds));
    }
    return query.fetch();
  }
}
