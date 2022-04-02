package com.familyorg.familyorganizationapp.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollOptionDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.DTO.VoteDto;
import com.familyorg.familyorganizationapp.DTO.builder.PollDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.PollOptionDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.domain.PollOption;
import com.familyorg.familyorganizationapp.domain.PollVote;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.id.VoteId;
import com.familyorg.familyorganizationapp.repository.PollOptionRepository;
import com.familyorg.familyorganizationapp.repository.PollRepository;
import com.familyorg.familyorganizationapp.repository.PollVoteRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.PollService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.DateUtil;

@Service
public class PollServiceImpl implements PollService {

  PollRepository pollRepository;
  PollOptionRepository pollOptionRepository;
  PollVoteRepository voteRepository;
  UserService userService;
  FamilyService familyService;

  @Autowired
  public PollServiceImpl(PollRepository pollRepository, PollOptionRepository pollOptionRepository,
      PollVoteRepository voteRepository, UserService userService, FamilyService familyService) {
    super();
    this.pollRepository = pollRepository;
    this.pollOptionRepository = pollOptionRepository;
    this.voteRepository = voteRepository;
    this.userService = userService;
    this.familyService = familyService;
  }

  @Override
  @Transactional
  public void createPoll(PollDto request) {
    if (request.getDescription() == null || request.getDescription().isBlank()
        || request.getClosedDateTime() == null || request.getClosedDateTime().isBlank()
        || request.getOptions() == null || request.getOptions().isEmpty()) {
      throw new BadRequestException(
          "A family id, description, closed date, and options are required to create a poll.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          "Family with id " + request.getFamilyId() + " not found.");
    }
    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to complete this action.");
    }
    Poll poll = new Poll();
    poll.setDescription(request.getDescription());
    poll.setNotes(request.getNotes());
    poll.setCreatedDatetime(Timestamp.from(Instant.now()));

    TimeZone timezone = familyService.getUserTimeZoneOrDefault(requestingUser, family.get());
    poll.setTimezone(timezone.getID());
    poll.setCloseDateTime(DateUtil.parseTimestamp(request.getClosedDateTime()));
    poll.setCreatedBy(requestingUser);
    poll.setFamily(family.get());
    poll.setOptions(request.getOptions().stream().map(option -> {
      PollOption pollOption = new PollOption();
      pollOption.setValue(option.getValue());
      return pollOption;
    }).filter(option -> option == null).collect(Collectors.toList()));

    Poll savedPoll = pollRepository.save(poll);
    voteRepository.saveAll(family.get().getMembers().stream().map(member -> {
      PollVote vote = new PollVote();
      vote.setPoll(savedPoll);
      vote.setUser(member.getUser());
      vote.setVote(null);
      return vote;
    }).collect(Collectors.toList()));
  }

  @Override
  @Transactional
  public void updatePoll(PollDto request) {
    if (request.getId() == null) {
      throw new BadRequestException("A poll id is required to update a poll.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(request.getId());
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + request.getId() + " not found.");
    }

    boolean hasAppropriatePrivileges = familyService
        .verfiyMinimumRoleSecurity(pollOpt.get().getFamily(), requestingUser, Role.ADMIN);

    if (!hasAppropriatePrivileges) {
      hasAppropriatePrivileges =
          pollOpt.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    }
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }

    Poll poll = pollOpt.get();

    // if there are any votes recorded we don't want to allow the user to change the poll
    if (poll.getOptions().stream().filter(option -> !option.getVotes().isEmpty()).count() > 0) {
      throw new BadRequestException(
          "A poll cannot be updated once users have started voting. Please delete this poll and "
              + "create a new one if the action was intended");
    }


    Map<Long, PollOptionDto> reqOptionsById =
        request.getOptions().stream().collect(Collectors.toMap(o -> o.getId(), o -> o));
    poll.setDescription(request.getDescription());
    poll.setNotes(request.getNotes());


    ListIterator<PollOption> iter = poll.getOptions().listIterator();
    while (iter.hasNext()) {
      PollOption curItem = iter.next();
      PollOptionDto requestOption = reqOptionsById.get(curItem.getId());
      if (requestOption != null) {
        curItem.setValue(requestOption.getValue());
      } else {
        iter.remove();
      }
    }

    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
    poll.setTimezone(timezone.getID());
    poll.setCloseDateTime(DateUtil.parseTimestamp(request.getClosedDateTime()));

    pollRepository.save(poll);
  }

  @Override
  @Transactional
  public void deletePoll(Long id) {
    if (id == null) {
      throw new BadRequestException("A poll id is required to delete it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges = familyService
        .verfiyMinimumRoleSecurity(pollOpt.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePrivileges) {
      hasAppropriatePrivileges =
          pollOpt.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    }
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }

    pollRepository.delete(pollOpt.get());
  }

  @Override
  public PollDto getPoll(Long id) {
    if (id == null) {
      throw new BadRequestException("A poll id is required to retrieve it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges = familyService
        .verfiyMinimumRoleSecurity(pollOpt.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }
    Poll poll = pollOpt.get();
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
    return new PollDtoBuilder().withId(poll.getId())
        .withFamilyId(poll.getFamily().getId())
        .withDescription(poll.getDescription())
        .withNotes(poll.getNotes())
        .withClosedDateTime(
            DateUtil.toTimezone(poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()),
                timezone))
        .withCreatedDateTime(
            DateUtil.toTimezone(poll.getCreatedDatetime(), TimeZone.getTimeZone(poll.getTimezone()),
                timezone))
        .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
        .withOptions(poll.getOptions()
            .stream()
            .map(option -> new PollOptionDtoBuilder().withId(option.getId())
                .withValue(option.getValue())
                .build())
            .collect(Collectors.toList()))
        .build();
  }

  @Override
  public List<PollDto> getPollsForFamily(Long id, boolean closed, boolean active) {
    if (id == null) {
      throw new BadRequestException("Poll id is required to retreive it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyService.getFamilyById(id);
    if (family.isEmpty()) {
      throw new ResourceNotFoundException("Family with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges = familyService
        .verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }
    List<Poll> polls = pollRepository.pollsByFamilyId(id, closed, active);

    return polls.stream()
        .map(poll -> {
          TimeZone timezone =
              familyService.getUserTimeZoneOrDefault(requestingUser, poll.getFamily());
          return new PollDtoBuilder().withId(poll.getId())
              .withFamilyId(poll.getFamily().getId())
              .withDescription(poll.getDescription())
              .withNotes(poll.getNotes())
              .withClosedDateTime(
                  DateUtil.toTimezone(poll.getCloseDateTime(),
                      TimeZone.getTimeZone(poll.getTimezone()),
                      timezone))
              .withCreatedDateTime(
                  DateUtil.toTimezone(poll.getCreatedDatetime(),
                      TimeZone.getTimeZone(poll.getTimezone()),
                      timezone))
              .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
              .withOptions(poll.getOptions()
                  .stream()
                  .map(option -> new PollOptionDtoBuilder().withId(option.getId())
                      .withValue(option.getValue())
                      .build())
                  .collect(Collectors.toList()))
              .build();
        })
        .collect(Collectors.toList());
  }

  @Override
  public List<PollDto> getUnvotedPolls() {
    User requestingUser = userService.getRequestingUser();
    List<PollVote> nullVotes = voteRepository.getUnvotedPolls(requestingUser.getId());
    if (nullVotes.isEmpty()) {
      return Collections.emptyList();
    }

    List<PollDto> response = nullVotes.stream().map(vote -> {
      Poll poll = vote.getPoll();
      TimeZone timezone =
          familyService.getUserTimeZoneOrDefault(requestingUser, poll.getFamily());
      return new PollDtoBuilder().withId(poll.getId())
          .withFamilyId(poll.getFamily().getId())
          .withDescription(poll.getDescription())
          .withNotes(poll.getNotes())
          .withClosedDateTime(
              DateUtil.toTimezone(poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()),
                  timezone))
          .withCreatedDateTime(
              DateUtil.toTimezone(poll.getCreatedDatetime(),
                  TimeZone.getTimeZone(poll.getTimezone()),
                  timezone))
          .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
          .withOptions(poll.getOptions()
              .stream()
              .map(option -> new PollOptionDtoBuilder().withId(option.getId())
                  .withValue(option.getValue())
                  .build())
              .collect(Collectors.toList()))
          .build();
    }).collect(Collectors.toList());
    return response;
  }

  @Override
  public PollDto getPollResults(Long id) {
    if (id == null) {
      throw new BadRequestException("A poll id is required to retrieve it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges = familyService
        .verfiyMinimumRoleSecurity(pollOpt.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }

    // TODO: add check for current progress
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
    Poll poll = pollOpt.get();
    return new PollDtoBuilder().withId(poll.getId())
        .withFamilyId(poll.getFamily().getId())
        .withDescription(poll.getDescription())
        .withNotes(poll.getNotes())
        .withClosedDateTime(
            DateUtil.toTimezone(poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()),
                timezone))
        .withCreatedDateTime(
            DateUtil.toTimezone(poll.getCreatedDatetime(), TimeZone.getTimeZone(poll.getTimezone()),
                timezone))
        .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
        .withOptions(poll.getOptions()
            .stream()
            .map(option -> new PollOptionDtoBuilder().withId(option.getId())
                .withValue(option.getValue())
                .withVotes(option.getVotes().size())
                .build())
            .collect(Collectors.toList()))
        .build();
  }

  @Override
  @Transactional
  public void vote(VoteDto request) {
    if (request.getChoice() == null || request.getChoice().getId() == null
        || request.getPollId() == null) {
      throw new BadRequestException("Both a poll and a option choice must be specified");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> poll = pollRepository.findById(request.getPollId());
    if (poll.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + request.getPollId() + " not found.");
    }

    VoteId voteId = new VoteId(poll.get().getId(), requestingUser.getId());
    Optional<PollVote> vote = voteRepository.findById(voteId);
    if (vote.isEmpty()) {
      throw new AuthorizationException(
          "User is not authorized to vote in this poll. Ask the poll creator to add you as a repondent.");
    }
    Optional<PollOption> choice = pollOptionRepository.findById(request.getChoice().getId());
    if (choice.isEmpty()) {
      throw new ResourceNotFoundException(
          "Poll option with id " + request.getChoice().getId() + " not found.");
    }
    vote.get().setVote(choice.get());
    voteRepository.save(vote.get());
  }

  @Override
  @Transactional
  public void updateRespondents(PollDto request) {
    if (request.getId() == null || request.getRespondents() == null
        || request.getRespondents().isEmpty()) {
      throw new BadRequestException("A poll id and list of respondents are required.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> poll = pollRepository.findById(request.getId());
    if (poll.isEmpty()) {
      throw new ResourceNotFoundException("Poll with id " + request.getId() + " not found.");
    }

    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(poll.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePrivileges) {
      hasAppropriatePrivileges =
          poll.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    }
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException("User is not authorized to perform this action.");
    }
    List<Long> respondentIds = poll.get()
        .getRespondents()
        .stream()
        .map(respondent -> respondent.getUser().getId())
        .collect(Collectors.toList());
    request.getRespondents().forEach(respondent -> {
      if (respondentIds.contains(respondent.getId())) {
        respondentIds.remove(respondent.getId());
      } else {
        User user = userService.getUserById(respondent.getId());
        if (user != null) {
          if (poll.get().getFamily().isMember(user)) {

            PollVote vote = new PollVote();
            vote.setPoll(poll.get());
            vote.setUser(user);
            vote.setVote(null);

            poll.get().getRespondents().add(vote);
          }
        }
      }
    });
    voteRepository.deleteAllByUserIdAndPoll(respondentIds, poll.get().getId());
  }

}
