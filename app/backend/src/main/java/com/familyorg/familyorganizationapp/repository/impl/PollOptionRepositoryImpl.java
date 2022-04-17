package com.familyorg.familyorganizationapp.repository.impl;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.PollOption;
import com.familyorg.familyorganizationapp.domain.QPollOption;
import com.familyorg.familyorganizationapp.repository.custom.PollOptionRepositoryCustom;
import com.querydsl.jpa.impl.JPADeleteClause;

@Repository
public class PollOptionRepositoryImpl extends QuerydslRepositorySupport
    implements PollOptionRepositoryCustom {

  QPollOption optionTable = QPollOption.pollOption;

  public PollOptionRepositoryImpl() {
    super(PollOption.class);
  }

  @Override
  public void deleteByPollId(Long pollId) {
    new JPADeleteClause(getEntityManager(), optionTable)
        .where(optionTable.poll.id.eq(pollId))
        .execute();
  }
}
