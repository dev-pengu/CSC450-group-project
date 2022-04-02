package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.PollVote;

public interface PollVoteRepositoryCustom {
  List<PollVote> getUnvotedPolls(Long userId);

  void deleteAllByUserIdAndPoll(List<Long> userIds, Long pollId);
}
