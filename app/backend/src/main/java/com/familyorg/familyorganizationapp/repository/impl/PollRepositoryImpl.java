package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.domain.QPoll;
import com.familyorg.familyorganizationapp.domain.QPollVote;
import com.familyorg.familyorganizationapp.repository.custom.PollRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class PollRepositoryImpl extends QuerydslRepositorySupport implements PollRepositoryCustom {

  QPoll pollTable = QPoll.poll;
  QPollVote respondentTable = QPollVote.pollVote;

  public PollRepositoryImpl() {
    super(Poll.class);
  }

  @Override
  public List<Poll> getFilteredPolls(List<Long> familyIdFilters, List<Long> pollIdFilters,
      boolean closed, boolean unvoted, Timestamp startDate, Timestamp endDate, long userId) {
    JPQLQuery<Poll> polls = from(pollTable)
        .where(pollTable.family.id.in(familyIdFilters));
    if (!pollIdFilters.isEmpty()) {
      polls.where(pollTable.id.in(pollIdFilters));
    }
    if (closed) {
      polls.where(pollTable.closeDateTime.lt(Timestamp.from(Instant.now())));
    }
    if (unvoted) {
      polls.innerJoin(pollTable.respondents, respondentTable);
      polls.where(respondentTable.user.id.eq(userId).and(respondentTable.vote.isNull()));
    }
    if (startDate != null) {
      polls.where(pollTable.closeDateTime.goe(startDate));
    } else {
      if (!closed) {
        polls.where(pollTable.closeDateTime.goe(Timestamp.from(Instant.now())));
      }
    }
    if (endDate != null) {
      polls.where(pollTable.closeDateTime.loe(endDate));
    }

    return polls.fetch();
  }
}
