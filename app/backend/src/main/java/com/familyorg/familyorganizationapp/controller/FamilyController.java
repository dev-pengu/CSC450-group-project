package com.familyorg.familyorganizationapp.controller;

import java.util.List;
import java.util.Optional;
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
import com.familyorg.familyorganizationapp.DTO.ErrorDto;
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.MemberInviteDto;
import com.familyorg.familyorganizationapp.DTO.builder.ErrorDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.FamilyNotFoundException;
import com.familyorg.familyorganizationapp.Exception.InviteCodeNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.service.MessagingService;

@RestController
@RequestMapping("/api/v1/family")
public class FamilyController {

  private static final Logger LOG = LoggerFactory.getLogger(FamilyController.class);

  @Autowired
  private FamilyService familyService;
  @Autowired
  private InviteService inviteService;
  @Autowired
  private MessagingService messagingService;

  FamilyController() {}

  /**
   * This constructor should only be used for testing to mock the injected classes
   *
   * @param familyService
   * @param inviteService
   * @param messagingService
   */
  FamilyController(FamilyService familyService, InviteService inviteService,
      MessagingService messagingService) {
    this.familyService = familyService;
    this.inviteService = inviteService;
    this.messagingService = messagingService;
  }

  @PostMapping()
  public ResponseEntity<?> createFamily(@RequestBody FamilyDto familyRequest) {
    try {
      FamilyDto createdFamily = familyService.createFamily(familyRequest);
      return new ResponseEntity<FamilyDto>(createdFamily, HttpStatus.CREATED);
    } catch (BadRequestException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.BAD_REQUEST.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/get-family")
  public ResponseEntity<?> getFamily(@RequestBody FamilyDto familyRequest) {
    try {
      FamilyDto family = familyService.getFamily(familyRequest);
      return new ResponseEntity<FamilyDto>(family, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get-family")
  public ResponseEntity<?> getFamilies(@RequestParam("userId") Long userId) {
    try {
      List<FamilyDto> families = familyService.getFamiliesByUser(userId);
      return new ResponseEntity<List<FamilyDto>>(families, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping()
  public ResponseEntity<?> updateFamily(@RequestBody FamilyDto familyRequest) {
    try {
      FamilyDto family = familyService.updateFamily(familyRequest);
      return new ResponseEntity<FamilyDto>(family, HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (BadRequestException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.BAD_REQUEST.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteFamily(@RequestParam("familyId") Long familyId) {
    try {
      familyService.deleteFamily(familyId);
      return new ResponseEntity<String>("Family successfully deleted", HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/invite")
  public ResponseEntity<?> getInvites(@RequestParam("id") Long familyId) {
    try {
      List<MemberInvite> invites = inviteService.getInvites(familyId);
      return new ResponseEntity<List<MemberInvite>>(invites, HttpStatus.OK);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/invite/generate")
  public ResponseEntity<?> generateInvite(@RequestBody MemberInviteDto memberInvite) {
    try {
      if (memberInvite.isPersistent()) {
        FamilyDto familyWithInviteCode =
            inviteService.generatePersistentMemberInvite(memberInvite.getFamilyId());
        return new ResponseEntity<FamilyDto>(familyWithInviteCode, HttpStatus.OK);
      } else {
        // Generate the Invite
        MemberInvite invite = null;
        if (memberInvite.getInitialRole() != null) {
          invite = inviteService.createUniqueMemberInviteWithRole(memberInvite.getFamilyId(),
              memberInvite.getRecipientEmail(), memberInvite.getInitialRole());
        } else {
          invite = inviteService.createUniqueMemberInvite(memberInvite.getFamilyId(),
              memberInvite.getRecipientEmail());
        }
        // Send the invite via email
        Optional<FamilyMembers> owner = invite.getFamily().getOwner();
        if (owner.isPresent()) {
          String emailContents = messagingService.buildInviteContent(
              invite.getInviteCodeObj().getInviteCodeString(), owner.get().getUser().getFullname());
          messagingService.sendHtmlEmail(invite.getUserEmail(), "You've been invited to a family!",
              emailContents);
        }
        // Assuming no errors were thrown, an invitation was sent successfully, respond with 200
        // status
        return new ResponseEntity<String>("Success", HttpStatus.OK);
      }
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/invite/join")
  public ResponseEntity<?> joinFamily(@RequestParam("code") String inviteCode,
      @RequestParam("eventColor") String eventColor) {
    try {
      InviteCode inviteCodeObj = InviteCode.parseFromCodeString(inviteCode);
      inviteService.verifyMemberInvite(inviteCodeObj, eventColor);
      return new ResponseEntity<String>("Success", HttpStatus.OK);
    } catch (UserNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).addRedirect("/register").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (InviteCodeNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (FamilyNotFoundException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.NOT_FOUND.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (AuthorizationException e) {
      LOG.error(e.getMessage(), e);
      ErrorDtoBuilder errorResponse = new ErrorDtoBuilder()
          .withErrorCode(HttpStatus.UNAUTHORIZED.value()).withMessage(e.getMessage());
      if (e.isRedirect()) {
        errorResponse = errorResponse.addRedirect("/login");
      }
      return new ResponseEntity<ErrorDto>(errorResponse.build(), HttpStatus.UNAUTHORIZED);
    } catch (BadRequestException e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse = new ErrorDtoBuilder().withErrorCode(HttpStatus.BAD_REQUEST.value())
          .withMessage(e.getMessage()).build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
      ErrorDto errorResponse =
          new ErrorDtoBuilder().withErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
              .withMessage("Error processing request.").build();
      return new ResponseEntity<ErrorDto>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
