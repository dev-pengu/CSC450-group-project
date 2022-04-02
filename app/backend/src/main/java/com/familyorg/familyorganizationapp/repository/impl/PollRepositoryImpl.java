package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.domain.QPoll;
import com.familyorg.familyorganizationapp.repository.custom.PollRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class PollRepositoryImpl extends QuerydslRepositorySupport implements PollRepositoryCustom {

  QPoll pollTable = QPoll.poll;

  public PollRepositoryImpl() {
    super(Poll.class);
  }

  @Override
  public List<Poll> pollsByFamilyId(Long id, boolean closed, boolean active) {
    JPQLQuery<Poll> polls = from(pollTable).where(pollTable.family.id.eq(id));
    if (closed) {
      polls.where(pollTable.closeDateTime.loe(Timestamp.from(Instant.now())));
    }
    if (active) {
      polls.where(pollTable.closeDateTime.goe(Timestamp.from(Instant.now())));
    }
    return polls.fetch();
  }
}
