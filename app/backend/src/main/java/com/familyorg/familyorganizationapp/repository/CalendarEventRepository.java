package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.repository.custom.CalendarEventRepositoryCustom;

public interface CalendarEventRepository
    extends CrudRepository<CalendarEvent, Long>, CalendarEventRepositoryCustom {
  // Spring data methods only
}
