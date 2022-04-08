package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.VoteDto;

public interface PollService {

  void createPoll(PollDto request);

  void updatePoll(PollDto request);

  void deletePoll(Long id);

  PollDto getPoll(Long id);

  void vote(VoteDto request);

  PollDto getPollResults(Long id);

  PollSearchResponseDto search(PollSearchRequestDto request);
}
