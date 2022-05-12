package com.familyorg.familyorganizationapp.DTO;

import java.util.Comparator;

public class EventDateComparator implements Comparator<CalendarEventDto> {


  public int compare(CalendarEventDto e1, CalendarEventDto e2) {
    return e1.getStartDate().compareTo(e2.getStartDate());
  }
}
