package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="family")
public class Family implements Serializable {

	private static final long serialVersionUID = 6681350428367318335L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name="name", columnDefinition="VARCHAR(50)", nullable=false)
	private String name;

	@Column(name="event_color", columnDefinition="VARCHAR(6)", nullable=false)
	private String eventColor;

	@Column(name="timezone", columnDefinition="VARCHAR(32)", nullable=false)
	private String timezone;

	@Column(name="invite_code", columnDefinition="VARCHAR(36)")
	private String inviteCode;

	@OneToMany(mappedBy="family", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<FamilyMembers> members;

	public Family() {
		super();
	}

	public Family(Long id, String name, String eventColor, String timezone, String inviteCode,
			Set<FamilyMembers> members) {
		super();
		this.id = id;
		this.name = name;
		this.eventColor = eventColor;
		this.timezone = timezone;
		this.inviteCode = inviteCode;
		this.members = members;
	}

	public Family(String name, String eventColor, String timezone, String inviteCode,
				  Set<FamilyMembers> members) {
		super();
		this.name = name;
		this.eventColor = eventColor;
		this.timezone = timezone;
		this.inviteCode = inviteCode;
		this.members = members;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEventColor() {
		return eventColor;
	}

	public void setEventColor(String eventColor) {
		this.eventColor = eventColor;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public Set<FamilyMembers> getMembers() {
		return members;
	}

	public void setMembers(Set<FamilyMembers> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return "Family [id=" + id + ", name=" + name + ", eventColor=" + eventColor + ", timezone=" + timezone
				+ ", inviteCode=" + inviteCode + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventColor, id, inviteCode, name, timezone);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Family other = (Family) obj;
		return Objects.equals(eventColor, other.eventColor) && Objects.equals(id, other.id)
				&& Objects.equals(inviteCode, other.inviteCode)
				&& Objects.equals(name, other.name) && Objects.equals(timezone, other.timezone);
	}


}
