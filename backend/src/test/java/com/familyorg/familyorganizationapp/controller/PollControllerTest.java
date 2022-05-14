package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.builder.PollDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.VoteDtoBuilder;
import com.familyorg.familyorganizationapp.service.PollService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class PollControllerTest {

  private PollService pollService;
  private PollController pollController;

  @BeforeEach
  public void setup() {
    pollService = mock(PollService.class);
    pollController = new PollController(pollService);
  }

  @Test
  public void createPoll_success() {
    /* Given */
    doNothing().when(pollService).createPoll(any());

    /* When */
    ResponseEntity<String> response = pollController.createPoll(new PollDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_createPoll_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(pollService).createPoll(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.createPoll(new PollDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updatePoll_success() {
    /* Given */
    when(pollService.updatePoll(any())).thenReturn("");

    /* When */
    ResponseEntity<String> response = pollController.updatePoll(new PollDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updatePoll_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(pollService).updatePoll(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.updatePoll(new PollDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getPoll_success() {
    /* Given */
    when(pollService.getPoll(any(Long.class))).thenReturn(new PollDtoBuilder().build());

    /* When */
    ResponseEntity<PollDto> response = pollController.getPoll(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getPoll_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(pollService.getPoll(any(Long.class))).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.getPoll(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deletePoll_success() {
    /* Given */
    doNothing().when(pollService).deletePoll(any());

    /* When */
    ResponseEntity<String> response = pollController.deletePoll(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deletePoll_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(pollService).deletePoll(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.deletePoll(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void search_success() {
    /* Given */
    when(pollService.search(any())).thenReturn(new PollSearchResponseDto());

    /* When */
    ResponseEntity<PollSearchResponseDto> response =
        pollController.search(new PollSearchRequestDto(true, false, null, null, null, true));

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_search_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(pollService.search(any(PollSearchRequestDto.class))).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.search(new PollSearchRequestDto(true, false, null, null, null, true));
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void vote_success() {
    /* Given */
    doNothing().when(pollService).vote(any());

    /* When */
    ResponseEntity<String> response = pollController.vote(new VoteDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_vote_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(pollService).vote(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.vote(new VoteDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void results_success() {
    /* Given */
    when(pollService.getPollResults(any(Long.class))).thenReturn(new PollDtoBuilder().build());

    /* When */
    ResponseEntity<PollDto> response = pollController.results(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_results_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(pollService.getPollResults(any(Long.class))).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          pollController.results(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }
}
