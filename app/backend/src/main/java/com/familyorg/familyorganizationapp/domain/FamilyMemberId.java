package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;

public class FamilyMemberId implements Serializable {
	private static final long serialVersionUID = 2559600146177437366L;
	long user;
	long family;
	
	public FamilyMemberId() {}
	
	public FamilyMemberId(long userId, long familyId) {
		this.user = userId;
		this.family = familyId;
	}

	public long getUser() {
		return user;
	}

	public void setUser(long user) {
		this.user = user;
	}

	public long getFamily() {
		return family;
	}

	public void setFamily(long family) {
		this.family = family;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		return Objects.hash(family, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FamilyMemberId other = (FamilyMemberId) obj;
		return family == other.family && user == other.user;
	}
	
	
}
