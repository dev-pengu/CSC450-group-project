package com.familyorg.familyorganizationapp.domain;

import com.familyorg.familyorganizationapp.domain.id.FamilyMemberId;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="family_members")
@IdClass(FamilyMemberId.class)
public class FamilyMembers implements Serializable {

	private static final long serialVersionUID = 3319029671945258246L;

	@Id
	@ManyToOne
	private User user;

	@Id
	@ManyToOne
	private Family family;

	@Column(name="role", columnDefinition="VARCHAR(10)", nullable=false)
	private Role role;

	@Column(name="event_color", columnDefinition="VARCHAR(6)", nullable=false)
	private String eventColor;



	public FamilyMembers() {
		super();
	}

	public FamilyMembers(User user, Family family, Role role, String eventColor) {
		super();
		this.user = user;
		this.family = family;
		this.role = role;
		this.eventColor = eventColor;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getEventColor() {
		return eventColor;
	}

	public void setEventColor(String eventColor) {
		this.eventColor = eventColor;
	}

	@Override
	public String toString() {
		return "FamilyMembers [user=" + user + ", family=" + family + ", role=" + role + ", eventColor=" + eventColor
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventColor, family, role, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FamilyMembers other = (FamilyMembers) obj;
		return Objects.equals(eventColor, other.eventColor) && Objects.equals(family, other.family)
				&& role == other.role && Objects.equals(user, other.user);
	}


}
