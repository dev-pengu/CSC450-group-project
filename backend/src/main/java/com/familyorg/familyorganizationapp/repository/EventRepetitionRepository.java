package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.EventRepetitionSchedule;
import com.familyorg.familyorganizationapp.repository.custom.EventRepetitionRepositoryCustom;

public interface EventRepetitionRepository
    extends CrudRepository<EventRepetitionSchedule, Long>, EventRepetitionRepositoryCustom {
  // Spring data methods only
}
