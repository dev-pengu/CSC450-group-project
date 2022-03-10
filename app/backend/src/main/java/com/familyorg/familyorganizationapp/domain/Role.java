package com.familyorg.familyorganizationapp.domain;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public enum Role {
	CHILD(0),
	ADULT(1),
	ADMIN(2),
	OWNER(3);

	private int level;

	Role(int level) {
		this.level = level;
	}

	public static Role fromLevel(int level) {
		Role[] roles = Role.values();
		Optional<Role> targetRole = Arrays.stream(roles).filter(role -> role.level == level).findFirst();
		if (targetRole.isPresent()) {
			return targetRole.get();
		}
		return null;
	}

	public static Role lowestLevelRole() {
		Role[] roles = Role.values();
		Comparator<Role> levelComparator = Comparator.comparingInt(Role::getLevel);
		Optional<Role> targetRole = Arrays.stream(roles).min(levelComparator);
		if (targetRole.isPresent()) {
			return targetRole.get();
		}
		return null;
	}

	public int getLevel() {
		return this.level;
	}
}
