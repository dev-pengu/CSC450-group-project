package com.familyorg.familyorganizationapp.repository.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.PollOption;
import com.familyorg.familyorganizationapp.repository.custom.PollOptionRepositoryCustom;

@Repository
public class PollOptionRepositoryImpl extends QuerydslRepositorySupport
    implements PollOptionRepositoryCustom {

  public PollOptionRepositoryImpl() {
    super(PollOption.class);
  }
}
