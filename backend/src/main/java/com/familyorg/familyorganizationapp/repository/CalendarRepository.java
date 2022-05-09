package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.repository.custom.CalendarRepositoryCustom;

public interface CalendarRepository
    extends CrudRepository<Calendar, Long>, CalendarRepositoryCustom {
  // Spring data methods only
}
