package com.familyorg.familyorganizationapp.service.impl;

import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.PollDto;
import com.familyorg.familyorganizationapp.DTO.PollOptionDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.PollSearchResponseDto;
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
import com.familyorg.familyorganizationapp.domain.search.PollField;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
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
  public PollServiceImpl(
      PollRepository pollRepository,
      PollOptionRepository pollOptionRepository,
      PollVoteRepository voteRepository,
      UserService userService,
      FamilyService familyService) {
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
    validatePollCreateRequest(request);
    User requestingUser = userService.getRequestingUser();

    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());
    if (family.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.FAMILY_DOESNT_EXIST,
          "Family with id " + request.getFamilyId() + " not found.");
    }
    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User is not authorized to complete this action.");
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

    Poll savedPoll = pollRepository.save(poll);
    List<PollOption> options =
        request.getOptions().stream()
            .map(
                option -> {
                  PollOption pollOption = new PollOption();
                  pollOption.setValue(option.getValue());
                  pollOption.setPoll(savedPoll);
                  return pollOption;
                })
            .collect(Collectors.toList());
    pollOptionRepository.saveAll(options);

    List<PollVote> respondents =
        request.getRespondents().isEmpty()
            ? family.get().getMembers().stream()
                .filter(
                    member ->
                        request.shouldOmitCreator()
                            && !Objects.equals(member.getUser().getId(), requestingUser.getId()))
                .map(
                    member -> {
                      PollVote vote = new PollVote();
                      vote.setPoll(savedPoll);
                      vote.setUser(member.getUser());
                      vote.setVote(null);
                      return vote;
                    })
                .collect(Collectors.toList())
            : request.getRespondents().stream()
                .map(
                    respondent -> {
                      User user = userService.getUserById(respondent.getId());
                      if (user != null) {
                        if (family.get().isMember(user)) {

                          PollVote vote = new PollVote();
                          vote.setPoll(savedPoll);
                          vote.setUser(user);
                          vote.setVote(null);
                          return vote;
                        }
                      }
                      return null;
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    voteRepository.saveAll(respondents);
  }

  @Override
  @Transactional
  public String updatePoll(PollDto request) {
    if (request.getId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A poll id is required to update a poll.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(request.getId());
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_DOESNT_EXIST, "Poll with id " + request.getId() + " not found.");
    }

    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(
            pollOpt.get().getFamily(), requestingUser, Role.ADMIN);

    if (!hasAppropriatePrivileges) {
      hasAppropriatePrivileges =
          pollOpt.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    }
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User is not authorized to perform this action.");
    }
    String responseMessage = "Poll updated successfully.";
    Poll poll = pollOpt.get();

    // if there are any votes recorded we don't want to allow the user to change the poll description or options
    // but adding respondents and changing notes is fine.
    boolean hasVotes = poll.getOptions().stream().anyMatch(option -> !option.getVotes().isEmpty());
    if (hasVotes) {
      responseMessage = "Poll respondents added and notes updated if applicable, but all other changes ignored." +
      "Cannot change a poll after votes have been cast.";
    }
    if (poll.getCloseDateTime().compareTo(Date.from(Instant.now())) < 0) {
      throw new BadRequestException(
          ApiExceptionCode.ILLEGAL_ACTION_REQUESTED, "A poll that is closed cannot be changed.");
    }

    if (!hasVotes) {
      // update options
      List<PollOption> reqOptions = request.getOptions().stream().map(option -> {
        PollOption opt = new PollOption();
        opt.setValue(option.getValue());
        opt.setId(option.getId());
        opt.setPoll(poll);
        return opt;
      }).collect(Collectors.toList());

      for (PollOption opt : reqOptions) {
        if (opt.getId() == null) {
          poll.addOption(opt);
        }
      }

      poll.getOptions().removeIf(opt -> !reqOptions.stream().map(PollOption::getId).toList().contains(opt.getId()));
      poll.setDescription(request.getDescription());
    }

    // update respondents on the poll
    if (request.getRespondents() != null && !request.getRespondents().isEmpty()) {
      List<VoteId> removed = poll.getRespondents().stream()
        .filter(resp ->
          !request.getRespondents().stream().map(UserDto::getId).toList().contains(resp.getUser().getId()))
        .map(PollVote::getId)
        .toList();
      voteRepository.deleteAllById(removed);

      for (UserDto resp : request.getRespondents()) {
        if (!poll.getRespondents().stream().map(r -> r.getUser().getId()).toList().contains(resp.getId())) {
          User user = userService.getUserById(resp.getId());
          if (user != null) {
            if (poll.getFamily().isMember(user)) {
              PollVote vote = new PollVote();
              vote.setPoll(poll);
              vote.setUser(user);
              vote.setVote(null);
              voteRepository.save(vote);
              poll.getRespondents().add(vote);
            }
          }
        }
      }
    }
    // update any of the other fields
    if (!hasVotes) {
      TimeZone timezone =
          familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
      poll.setTimezone(timezone.getID());
      poll.setCloseDateTime(DateUtil.parseTimestamp(request.getClosedDateTime()));
      poll.setDescription(request.getDescription());
    }
    poll.setNotes(request.getNotes());

    // save the poll and send back the message
    pollRepository.save(poll);
    return responseMessage;
  }

  @Override
  @Transactional
  public void deletePoll(Long id) {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A poll id is required to delete it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_DOESNT_EXIST, "Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(
            pollOpt.get().getFamily(), requestingUser, Role.ADMIN);
    if (!hasAppropriatePrivileges) {
      hasAppropriatePrivileges =
          pollOpt.get().getCreatedBy().getUsername().equals(requestingUser.getUsername());
    }
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User is not authorized to perform this action.");
    }
    voteRepository.deleteByPollId(id);
    pollOptionRepository.deleteByPollId(id);
    pollRepository.delete(pollOpt.get());
  }

  @Override
  public PollDto getPoll(Long id) {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A poll id is required to retrieve it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_DOESNT_EXIST, "Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(
            pollOpt.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User is not authorized to perform this action.");
    }
    Poll poll = pollOpt.get();
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
    return new PollDtoBuilder()
        .withId(poll.getId())
        .withFamilyId(poll.getFamily().getId())
        .withDescription(poll.getDescription())
        .withNotes(poll.getNotes())
        .withClosedDateTime(
            DateUtil.toTimezone(
                poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()), timezone))
        .withCreatedDateTime(
            DateUtil.toTimezone(
                poll.getCreatedDatetime(), TimeZone.getTimeZone(poll.getTimezone()), timezone))
        .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
        .withOptions(
            poll.getOptions().stream()
                .map(
                    option ->
                        new PollOptionDtoBuilder()
                            .withId(option.getId())
                            .withValue(option.getValue())
                            .build())
                .collect(Collectors.toList()))
        .isClosed(
            DateUtil.toZonedDateTime(
                        poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()), timezone)
                    .compareTo(ZonedDateTime.now(ZoneId.of(timezone.getID())))
                < 0)
        .withRespondents(
            poll.getRespondents().stream()
                .map(respondent -> UserDto.fromUserObj(respondent.getUser()))
                .collect(Collectors.toList()))
        .build();
  }

  @Override
  public PollDto getPollResults(Long id) {
    if (id == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "A poll id is required to retrieve it.");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> pollOpt = pollRepository.findById(id);
    if (pollOpt.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_DOESNT_EXIST, "Poll with id " + id + " not found.");
    }

    boolean hasAppropriatePrivileges =
        familyService.verfiyMinimumRoleSecurity(
            pollOpt.get().getFamily(), requestingUser, Role.CHILD);
    if (!hasAppropriatePrivileges) {
      throw new AuthorizationException(
          ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,
          "User is not authorized to perform this action.");
    }

    boolean allVotesIn =
        pollOpt.get().getRespondents().stream()
            .allMatch(respondent -> respondent.getVote() != null);
    System.out.println(allVotesIn);
    System.out.println(pollOpt.get().getCloseDateTime());
    System.out.println(Date.from(Instant.now()));
    boolean closed = pollOpt.get().getCloseDateTime().compareTo(Date.from(Instant.now())) < 0;
    if (!allVotesIn && !closed) {
      throw new BadRequestException(
          ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
          "Results cannot be viewed for a poll still in progress.");
    }
    TimeZone timezone =
        familyService.getUserTimeZoneOrDefault(requestingUser, pollOpt.get().getFamily());
    Poll poll = pollOpt.get();
    return new PollDtoBuilder()
        .withId(poll.getId())
        .withFamilyId(poll.getFamily().getId())
        .withDescription(poll.getDescription())
        .withNotes(poll.getNotes())
        .withClosedDateTime(
            DateUtil.toTimezone(
                poll.getCloseDateTime(), TimeZone.getTimeZone(poll.getTimezone()), timezone))
        .withCreatedDateTime(
            DateUtil.toTimezone(
                poll.getCreatedDatetime(), TimeZone.getTimeZone(poll.getTimezone()), timezone))
        .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
        .withOptions(
            poll.getOptions().stream()
                .map(
                    option ->
                        new PollOptionDtoBuilder()
                            .withId(option.getId())
                            .withValue(option.getValue())
                            .withVotes(option.getVotes().size())
                            .build())
                .collect(Collectors.toList()))
        .build();
  }

  @Override
  @Transactional
  public void vote(VoteDto request) {
    if (request.getChoice() == null
        || request.getChoice().getId() == null
        || request.getPollId() == null) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING,
          "Both a poll and a option choice must be specified");
    }
    User requestingUser = userService.getRequestingUser();

    Optional<Poll> poll = pollRepository.findById(request.getPollId());
    if (poll.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_DOESNT_EXIST,
          "Poll with id " + request.getPollId() + " not found.");
    }

    VoteId voteId = new VoteId(poll.get().getId(), requestingUser.getId());
    Optional<PollVote> vote = voteRepository.findById(voteId);
    if (vote.isEmpty()) {
      throw new AuthorizationException(
          ApiExceptionCode.ACTION_NOT_PERMITTED,
          "User is not authorized to vote in this poll. Ask the poll creator to add you as a repondent.");
    }
    Optional<PollOption> choice = pollOptionRepository.findById(request.getChoice().getId());
    if (choice.isEmpty()) {
      throw new ResourceNotFoundException(
          ApiExceptionCode.POLL_OPTION_DOESNT_EXIST,
          "Poll option with id " + request.getChoice().getId() + " not found.");
    }
    vote.get().setVote(choice.get());
    voteRepository.save(vote.get());
  }

  @Override
  public PollSearchResponseDto search(PollSearchRequestDto request) {
    verifyPollSearchRequest(request);
    User requestingUser = userService.getRequestingUser();

    PollSearchResponseDto response = new PollSearchResponseDto();
    response.setStart(request.getStart());
    response.setEnd(request.getEnd());
    response.setActiveSearchFilters(request.getFilters());

    List<Long> permittedFamilyIds = familyService.getFamilyIdsByUser(requestingUser.getUsername());
    List<Long> requestFamilyIds = request.getIdsByField(PollField.FAMILY);
    List<Poll> polls =
        pollRepository.getFilteredPolls(
            requestFamilyIds.isEmpty()
                ? permittedFamilyIds
                : permittedFamilyIds.stream()
                    .filter(requestFamilyIds::contains)
                    .collect(Collectors.toList()),
            request.getIdsByField(PollField.POLL),
            request.getClosed(),
            request.getUnVoted(),
            request.getStart() == null ? null : new Timestamp(request.getStart().getTime()),
            request.getEnd() == null ? null : new Timestamp(request.getEnd().getTime()),
            requestingUser.getId());

    if (request.shouldLimitToCreated()) {
      polls = polls.stream().filter(poll -> {
        if (familyService.verfiyMinimumRoleSecurity(poll.getFamily(), requestingUser, Role.ADMIN)) {
          return true;
        } else if (poll.getCreatedBy().getId().equals(requestingUser.getId())) {
          return true;
        }
        return false;
      }).collect(Collectors.toList());
    }

    TimeZone timezone = TimeZone.getTimeZone(requestingUser.getTimezone());
    response.setSearchFilters(getSearchFilters(polls));
    response.setPolls(
        polls.stream()
            .map(
                poll ->
                    new PollDtoBuilder()
                        .withId(poll.getId())
                        .withFamilyId(poll.getFamily().getId())
                        .setFamilyName(poll.getFamily().getName())
                        .withDescription(poll.getDescription())
                        .withNotes(poll.getNotes())
                        .withClosedDateTime(
                            DateUtil.toTimezone(
                                poll.getCloseDateTime(),
                                TimeZone.getTimeZone(poll.getTimezone()),
                                timezone))
                        .withCreatedDateTime(
                            DateUtil.toTimezone(
                                poll.getCreatedDatetime(),
                                TimeZone.getTimeZone(poll.getTimezone()),
                                timezone))
                        .withCreatedBy(UserDto.fromUserObj(poll.getCreatedBy()))
                        .withOptions(
                            poll.getOptions().stream()
                                .map(
                                    option ->
                                        new PollOptionDtoBuilder()
                                            .withId(option.getId())
                                            .withValue(option.getValue())
                                            .build())
                                .collect(Collectors.toList()))
                        .isClosed(
                            DateUtil.toZonedDateTime(
                                        poll.getCloseDateTime(),
                                        TimeZone.getTimeZone(poll.getTimezone()),
                                        timezone)
                                    .compareTo(ZonedDateTime.now(ZoneId.of(timezone.getID())))
                                < 0)
                        .withRespondents(
                            poll.getRespondents().stream()
                                .map(respondent -> UserDto.fromUserObj(respondent.getUser()))
                                .collect(Collectors.toList()))
                        .setVote(
                            poll.getRespondents().stream()
                                .filter(respondent -> respondent.getUser().getId().equals(requestingUser.getId()))
                                .findFirst()
                                .orElse(null))
                        .build())
            .collect(Collectors.toList()));

    return response;
  }

  private void verifyPollSearchRequest(PollSearchRequestDto request) {}

  private Map<PollField, List<SearchFilter>> getSearchFilters(List<Poll> polls) {
    Map<PollField, List<SearchFilter>> filters = new HashMap<>();
    filters.put(
        PollField.POLL,
        polls.stream()
            .map(poll -> new SearchFilter(poll.getId(), poll.getDescription()))
            .collect(Collectors.toList()));
    filters.put(
        PollField.FAMILY,
        polls.stream()
            .map(poll -> new SearchFilter(poll.getFamily().getId(), poll.getFamily().getName()))
            .distinct()
            .collect(Collectors.toList()));

    return filters;
  }

  private void validatePollCreateRequest(PollDto request) {

    if (request.getDescription() == null || request.getDescription().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Description is required to create a poll.");
    }
    if (request.getClosedDateTime() == null || request.getClosedDateTime().isBlank()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Closing date is required to create a poll");
    }
    if (request.getOptions() == null || request.getOptions().isEmpty()) {
      throw new BadRequestException(
          ApiExceptionCode.REQUIRED_PARAM_MISSING, "Options are required to create a poll.");
    }

    if (DateUtil.parseDateTime(request.getClosedDateTime()).compareTo(Date.from(Instant.now()))
        < 0) {
      throw new BadRequestException(
          ApiExceptionCode.BAD_PARAM_VALUE,
          "Closed date occurs in the past. Cannot create a poll that is already closed.");
    }
  }
}
