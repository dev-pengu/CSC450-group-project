package com.familyorg.familyorganizationapp.DTO.builder;

import java.util.HashSet;
import java.util.Set;

import com.familyorg.familyorganizationapp.DTO.FamilyDto;
import com.familyorg.familyorganizationapp.DTO.FamilyMemberDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.Role;

public class FamilyDtoBuilder {
	private Long id;
	private String name;
	private String eventColor;
	private String timezone;
	private String inviteCode;
	private Set<FamilyMemberDto> members;
	private FamilyMemberDto owner;
	private UserDto requestingUser;
	
	public FamilyDtoBuilder withId(Long id) {
		this.id = id;
		return this;
	}
	
	public FamilyDtoBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	public FamilyDtoBuilder withEventColor(String eventColor) {
		this.eventColor = eventColor;
		return this;
	}
	
	public FamilyDtoBuilder withTimezone(String timezone) {
		this.timezone = timezone;
		return this;
	}
	
	public FamilyDtoBuilder withInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
		return this;
	}
	
	public FamilyDtoBuilder withOwner(FamilyMemberDto owner) {
		this.owner = owner;
		return this;
	}
	
	public FamilyDtoBuilder withRequestingUser(UserDto requestingUser) {
		this.requestingUser = requestingUser;
		return this;
	}
	
	public FamilyDtoBuilder withMembers(Set<FamilyMemberDto> members) {
		for(FamilyMemberDto member : members) {
			if (member.getRole().equals(Role.OWNER) && this.owner == null) {
				this.owner = member;
				break;
			}
		}
		this.members = members;
		return this;
	}
	
	public FamilyDtoBuilder addMember(FamilyMemberDto member) {
		if (this.members == null) {
			this.members = new HashSet<>();
		}
		if (member.getRole().equals(Role.OWNER) && this.owner == null) {
			this.owner = member;
		}
			
		this.members.add(member);
		return this;
	}
	
	
	public void setId(Long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEventColor(String eventColor) {
		this.eventColor = eventColor;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
	public void setMembers(Set<FamilyMemberDto> members) {
		for(FamilyMemberDto member : members) {
			if (member.getRole().equals(Role.OWNER)) {
				this.owner = member;
				break;
			}
		}
		this.members = members;
	}
	public void setOwner(FamilyMemberDto owner) {
		this.owner = owner;
	}
	public void setRequestingUser(UserDto user) {
		this.requestingUser = user;
	}
	
	public void addMember(FamilyMembers member) {
		if (this.members == null) {
			this.members = new HashSet<>();
		}
		UserDto user = new UserDto(
				member.getUser().getId(),
				member.getUser().getFirstName(),
				member.getUser().getLastName(),
				member.getUser().getEmail(),
				member.getUser().getUsername());
		FamilyMemberDto memberDto = new FamilyMemberDto(
				user, 
				member.getEventColor(),
				member.getFamily().getId(), 
				member.getRole());
		this.members.add(memberDto);
		if (member.getRole().equals(Role.OWNER)) {
			this.owner = memberDto;
		}
	}
	
	public FamilyDto build() {
		return new FamilyDto(
				this.id,
				this.name,
				this.eventColor,
				this.timezone,
				this.inviteCode,
				this.owner,
				this.members,
				this.requestingUser);
	}
}
