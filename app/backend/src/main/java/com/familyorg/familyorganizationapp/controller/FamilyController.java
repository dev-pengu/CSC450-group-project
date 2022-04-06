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
import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyRoleUpdateRequest;
import com.familyorg.familyorganizationapp.DTO.MemberInviteDto;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.InviteCode;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.InviteService;
import com.familyorg.familyorganizationapp.service.MessagingService;

@RestController
@RequestMapping("/api/v1/family")

public class FamilyController {

  private static final Logger logger = LoggerFactory.getLogger(FamilyController.class);

  private FamilyService familyService;
  private InviteService inviteService;
  private MessagingService messagingService;

  @Autowired
  FamilyController(FamilyService familyService, InviteService inviteService,
      MessagingService messagingService) {
    this.familyService = familyService;
    this.inviteService = inviteService;
    this.messagingService = messagingService;
  }

  @PostMapping()
  public ResponseEntity<FamilyDto> createFamily(@RequestBody FamilyDto familyRequest) {
    FamilyDto createdFamily = familyService.createFamily(familyRequest);
    return new ResponseEntity<FamilyDto>(createdFamily, HttpStatus.CREATED);
  }

  @PostMapping("/get-family")
  public ResponseEntity<FamilyDto> getFamily(@RequestBody FamilyDto familyRequest) {
    FamilyDto family = familyService.getFamily(familyRequest);
    return new ResponseEntity<FamilyDto>(family, HttpStatus.OK);

  }

  @GetMapping("/get-family")
  public ResponseEntity<List<FamilyDto>> getFamilies() {
    List<FamilyDto> families = familyService.getFamiliesByUser();
    return new ResponseEntity<List<FamilyDto>>(families, HttpStatus.OK);
  }

  @GetMapping("/invite/join")
  public ResponseEntity<String> joinFamily(@RequestParam("code") String inviteCode,
      @RequestParam("eventColor") String eventColor) {
    InviteCode inviteCodeObj = InviteCode.parseFromCodeString(inviteCode);
    inviteService.verifyMemberInvite(inviteCodeObj, eventColor);
    return new ResponseEntity<String>("Success", HttpStatus.OK);
  }

  @PatchMapping("/admin/update")
  public ResponseEntity<FamilyDto> updateFamily(@RequestBody FamilyDto familyRequest) {
    FamilyDto family = familyService.updateFamily(familyRequest);
    return new ResponseEntity<FamilyDto>(family, HttpStatus.OK);
  }

  @DeleteMapping("/admin/delete")
  public ResponseEntity<String> deleteFamily(@RequestParam("familyId") Long familyId) {
    familyService.deleteFamily(familyId);
    return new ResponseEntity<String>("Family successfully deleted", HttpStatus.OK);

  }

  @PatchMapping("/admin/transferOwnership")
  public ResponseEntity<FamilyDto> transferOwnership(@RequestBody FamilyDto familyRequest) {
    FamilyDto response = familyService.transferOwnership(familyRequest);
    return new ResponseEntity<FamilyDto>(response, HttpStatus.OK);
  }

  @GetMapping("/admin/invites")
  public ResponseEntity<List<MemberInvite>> getInvites(@RequestParam("id") Long familyId) {
    List<MemberInvite> invites = inviteService.getInvites(familyId);
    return new ResponseEntity<List<MemberInvite>>(invites, HttpStatus.OK);
  }

  @PostMapping("/admin/invites/generate")
  public ResponseEntity<?> generateInvite(@RequestBody MemberInviteDto memberInvite) {
    if (memberInvite.isPersistent()) {
      FamilyDto familyWithInviteCode =
          inviteService.generatePersistentMemberInvite(memberInvite.getFamilyId());
      return new ResponseEntity<String>("Invite code created successfully", HttpStatus.OK);
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
  }

  @PostMapping("/admin/roles")
  public ResponseEntity<String> updateRoles(@RequestBody FamilyRoleUpdateRequest request) {
    familyService.updateMemberRoles(request);
    return new ResponseEntity<String>("Member roles successfully updated.", HttpStatus.OK);
  }
}
