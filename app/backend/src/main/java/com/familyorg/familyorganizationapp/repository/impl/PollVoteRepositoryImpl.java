package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.PollVote;
import com.familyorg.familyorganizationapp.domain.QPollVote;
import com.familyorg.familyorganizationapp.repository.custom.PollVoteRepositoryCustom;
import com.querydsl.jpa.impl.JPADeleteClause;

@Repository
public class PollVoteRepositoryImpl extends QuerydslRepositorySupport
    implements PollVoteRepositoryCustom {

  QPollVote voteTable = QPollVote.pollVote;

  public PollVoteRepositoryImpl() {
    super(PollVote.class);
  }

  @Override
  public List<PollVote> getUnvotedPolls(Long userId) {
    List<PollVote> votes =
        from(voteTable)
            .where(voteTable.user.id.eq(userId)
                .and(voteTable.vote.isNull())
                .and(voteTable.poll.closeDateTime.goe(Timestamp.from(Instant.now()))))
            .fetch();
    return votes;
  }

  @Override
  public void deleteOpenPollsForFamilyByUser(Long userId, Long familyId) {
    new JPADeleteClause(getEntityManager(), voteTable)
      .where(voteTable.user.id.eq(userId).and(voteTable.poll.family.id.eq(familyId)))
      .execute();
  }

  @Override
  public void deleteAllByUserIdAndPoll(List<Long> userIds, Long pollId) {
    new JPADeleteClause(getEntityManager(), voteTable)
        .where(voteTable.user.id.in(userIds)
            .and(voteTable.poll.id.eq(pollId)))
        .execute();
  }

  @Override
  public void deleteByPollId(Long pollId) {
    new JPADeleteClause(getEntityManager(), voteTable)
        .where(voteTable.poll.id.eq(pollId))
        .execute();
  }
}
