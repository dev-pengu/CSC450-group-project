package com.familyorg.familyorganizationapp.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class InviteCodeTest {

	@Test
	public void test_persistent_code_generation_from_string() {
		/* Given */
		String inviteCodeString = "31d7ad05-3352-4f67-8730-274b17e1da3d";
		
		/* When */
		InviteCode inviteCode = InviteCode.parseFromCodeString(InviteCode.PERSISTENT_PREFIX + "-" + inviteCodeString);
		
		/* Then */
		assertNotNull(inviteCode);
		assertEquals(UUID.fromString(inviteCodeString), inviteCode.getCode());
		assertTrue(inviteCode.getInviteCodeString().contains(InviteCode.PERSISTENT_PREFIX));
		assertTrue(inviteCode.isPersistent());
		assertEquals(inviteCodeString, inviteCode.toString());
	}
  
	@Test
	public void test_one_time_use_code_generation_from_string() {
		/* Given */
		String inviteCodeString = "31d7ad05-3352-4f67-8730-274b17e1da3d";
		
		/* When */
		InviteCode inviteCode = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + inviteCodeString);
		
		/* Then */
		assertNotNull(inviteCode);
		assertEquals(UUID.fromString(inviteCodeString), inviteCode.getCode());
		assertTrue(inviteCode.getInviteCodeString().contains(InviteCode.ONE_TIME_USE_PREFIX));
		assertFalse(inviteCode.isPersistent());
		assertEquals(inviteCodeString, inviteCode.toString());
	}
	
	@Test
	public void test_code_generation() {
		/* Given */
		InviteCode inviteCode = new InviteCode();
		
		/* When */
		inviteCode.generate();
		
		/* Then */
		assertNotNull(inviteCode.getCode());
	}
	
	@Test
	public void when_code_and_persistence_equal_then_invite_codes_equal() {
		/* Given */
		String inviteCodeString = "31d7ad05-3352-4f67-8730-274b17e1da3d";
		InviteCode inviteCode1 = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + inviteCodeString);
		InviteCode inviteCode2 = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + inviteCodeString);
		
		/* Then */
		assertTrue(inviteCode1.equals(inviteCode2));
	}
	
	@Test
	public void when_persistence_not_equal_then_invite_codes_not_equal() {
		/* Given */
		String inviteCodeString = "31d7ad05-3352-4f67-8730-274b17e1da3d";
		InviteCode inviteCode1 = InviteCode.parseFromCodeString(InviteCode.ONE_TIME_USE_PREFIX + "-" + inviteCodeString);
		InviteCode inviteCode2 = InviteCode.parseFromCodeString(InviteCode.PERSISTENT_PREFIX + "-" + inviteCodeString);
		
		/* Then */
		assertFalse(inviteCode1.equals(inviteCode2));
	}
	
	@Test
	public void when_code_not_equal_then_invite_codes_not_equal() {
		/* Given */
		InviteCode inviteCode1 = new InviteCode(true);
		InviteCode inviteCode2 = new InviteCode(true);
		
		/* Then */
		assertFalse(inviteCode1.equals(inviteCode2));
	}
	
	@Test
	public void when_created_with_persistence_arg_then_code_generated() {
		/* Given */
		InviteCode persistentCode = new InviteCode(true);
		InviteCode oneTimeUseCode = new InviteCode(false);
		
		/* Then */
		assertNotNull(persistentCode.getCode());
		assertNotNull(oneTimeUseCode.getCode());
		assertTrue(persistentCode.getInviteCodeString().contains(InviteCode.PERSISTENT_PREFIX));
		assertTrue(oneTimeUseCode.getInviteCodeString().contains(InviteCode.ONE_TIME_USE_PREFIX));
	}
	
	@Test
	public void when_invite_code_parsed_from_generated_invite_code_then_codes_equal() {
		/* Given */
		InviteCode inviteCode = new InviteCode(true);
		String inviteCodeString = inviteCode.getInviteCodeString();
		
		/* When */
		InviteCode parsedInviteCode = InviteCode.parseFromCodeString(inviteCodeString);
		
		/* Then */
		assertTrue(inviteCode.equals(parsedInviteCode));
	}
}
