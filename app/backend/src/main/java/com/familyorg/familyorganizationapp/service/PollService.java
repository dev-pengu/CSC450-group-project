package com.familyorg.familyorganizationapp.service;

import java.util.List;
import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.VoteDto;

public interface PollService {

  void createPoll(PollDto request);

  void updatePoll(PollDto request);

  void deletePoll(Long id);

  PollDto getPoll(Long id);

  List<PollDto> getUnvotedPolls();

  void vote(VoteDto request);

  PollDto getPollResults(Long id);

  List<PollDto> getPollsForFamily(Long id, boolean closed, boolean active);

  void updateRespondents(PollDto request);
}
