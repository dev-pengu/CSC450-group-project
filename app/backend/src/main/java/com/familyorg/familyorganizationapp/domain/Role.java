package com.familyorg.familyorganizationapp.domain;

public enum Role {
	CHILD(0),
	ADULT(1),
	ADMIN(2),
	OWNER(3);
	
	private int level;
	
	Role(int level) {
		this.level = level;
	}
	
	public int getLevel() {
		return this.level;
	}
}
