package com.familyorg.familyorganizationapp.repository.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.EventRepetitionSchedule;
import com.familyorg.familyorganizationapp.repository.custom.EventRepetitionRepositoryCustom;

@Repository
public class EventRepetitionRepositoryImpl extends QuerydslRepositorySupport
    implements EventRepetitionRepositoryCustom {

  public EventRepetitionRepositoryImpl() {
    super(EventRepetitionSchedule.class);
  }
}
