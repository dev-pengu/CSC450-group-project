package com.familyorg.familyorganizationapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.VoteDto;
import com.familyorg.familyorganizationapp.service.PollService;

@RestController
@RequestMapping("/api/v1/poll")
public class PollController {

  private Logger logger = LoggerFactory.getLogger(PollController.class);

  private PollService pollService;

  @Autowired
  public PollController(PollService pollService) {
    this.pollService = pollService;
  }

  @PostMapping()
  public ResponseEntity<String> createPoll(@RequestBody() PollDto request) {
    pollService.createPoll(request);
    return new ResponseEntity<String>("Poll created successfully.", HttpStatus.CREATED);
  }

  @PatchMapping()
  public ResponseEntity<String> updatePoll(@RequestBody() PollDto request) {
    pollService.updatePoll(request);
    return new ResponseEntity<String>("Poll updated successfully.", HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<PollDto> getPoll(@RequestParam("id") Long pollId) {
    PollDto poll = pollService.getPoll(pollId);
    return new ResponseEntity<PollDto>(poll, HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<String> deletePoll(@RequestParam("id") Long pollId) {
    pollService.deletePoll(pollId);
    return new ResponseEntity<String>("Poll deleted successfully.", HttpStatus.OK);
  }

  /**
   * Search for polls. If closed is false and no start date is specified, the current timestamp will
   * be used.
   *
   * @param request
   * @return
   */
  @PostMapping("/search")
  public ResponseEntity<PollSearchResponseDto> search(@RequestBody() PollSearchRequestDto request) {
    PollSearchResponseDto response = pollService.search(request);
    return new ResponseEntity<PollSearchResponseDto>(response, HttpStatus.OK);
  }

  @PostMapping("/vote")
  public ResponseEntity<String> vote(@RequestBody() VoteDto request) {
    pollService.vote(request);
    return new ResponseEntity<String>("Vote recorded successfully", HttpStatus.OK);
  }

  @GetMapping("/results")
  public ResponseEntity<PollDto> results(@RequestParam("id") Long pollId) {
    PollDto response = pollService.getPollResults(pollId);
    return new ResponseEntity<PollDto>(response, HttpStatus.OK);
  }
}
