package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.Calendar;

public interface CalendarRepositoryCustom {

  List<Calendar> calendarDataByFamilyIds(List<Long> familyIds);

  List<Calendar> search(List<Long> familyIds, List<Long> calendarIds);
}
