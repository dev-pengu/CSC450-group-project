package com.familyorg.familyorganizationapp.service.impl;

import static org.mockito.Mockito.mock;

import com.familyorg.familyorganizationapp.repository.PollOptionRepository;
import com.familyorg.familyorganizationapp.repository.PollRepository;
import com.familyorg.familyorganizationapp.repository.PollVoteRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.PollService;
import com.familyorg.familyorganizationapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;

public class PollServiceImplTest {

  private PollRepository pollRepository;
  private PollOptionRepository optionRepository;
  private PollVoteRepository voteRepository;
  private UserService userService;
  private FamilyService familyService;
  private PollService pollService;

  @BeforeEach
  public void setup() {
    pollRepository = mock(PollRepository.class);
    optionRepository = mock(PollOptionRepository.class);
    voteRepository = mock(PollVoteRepository.class);
    userService = mock(UserService.class);
    familyService = mock(FamilyService.class);
    pollService =
        new PollServiceImpl(
            pollRepository, optionRepository, voteRepository, userService, familyService);
  }
}
