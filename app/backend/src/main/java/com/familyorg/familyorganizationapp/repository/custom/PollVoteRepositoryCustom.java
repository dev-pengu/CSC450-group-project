package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.PollVote;

public interface PollVoteRepositoryCustom {
  List<PollVote> getUnvotedPolls(Long userId);

  void deleteOpenPollsForFamilyByUser(Long userId, Long familyId);

  void deleteAllByUserIdAndPoll(List<Long> userIds, Long pollId);

  List<PollVote> getVotesForDeletionByUserAndFamily(Long userId, Long familyId);

  void deleteByPollId(Long pollId);
}
