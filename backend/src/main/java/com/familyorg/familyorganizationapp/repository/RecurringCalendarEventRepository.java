package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.repository.custom.RecurringCalendarEventRepositoryCustom;

public interface RecurringCalendarEventRepository
    extends CrudRepository<RecurringCalendarEvent, Long>, RecurringCalendarEventRepositoryCustom {
  // Spring data methods only
}
