package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
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
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "family")
public class Family implements Serializable {

  private static final long serialVersionUID = 6681350428367318335L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "family_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false)
  private String name;

  @Column(name = "event_color", columnDefinition = "VARCHAR(6)", nullable = false)
  private String eventColor;

  @Column(name = "timezone", columnDefinition = "VARCHAR(256)", nullable = false)
  private String timezone;

  @Column(name = "invite_code", columnDefinition = "VARCHAR(36)")
  private String inviteCode;

  @JsonIgnore
  @OneToMany(
      mappedBy = "family",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<FamilyMembers> members;

  @JsonIgnore
  @OneToMany(
      mappedBy = "family",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<Calendar> calendars;

  @JsonIgnore
  @OneToMany(
      mappedBy = "family",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ShoppingList> shoppingLists;

  @JsonIgnore
  @OneToMany(
      mappedBy = "family",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private Set<ToDoList> toDoLists;

  public Family() {}

  public Family(
      Long id,
      String name,
      String eventColor,
      String timezone,
      String inviteCode,
      Set<FamilyMembers> members) {
    this.id = id;
    this.name = name;
    this.eventColor = eventColor;
    this.timezone = timezone;
    this.inviteCode = inviteCode;
    this.members = members;
  }

  public Family(
      String name,
      String eventColor,
      String timezone,
      String inviteCode,
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
    return this.inviteCode;
  }

  public InviteCode getInviteCodeObj() {
    if (inviteCode == null) {
      return new InviteCode();
    }
    return InviteCode.parseFromCodeString(InviteCode.PERSISTENT_PREFIX + "-" + this.inviteCode);
  }

  public void setInviteCode(String inviteCode) {
    this.inviteCode = inviteCode;
  }

  public void setInviteCode(InviteCode inviteCode) {
    this.inviteCode = inviteCode.toString();
  }

  public Set<FamilyMembers> getMembers() {
    return members;
  }

  public void setMembers(Set<FamilyMembers> members) {
    this.members = members;
  }

  public void addMember(FamilyMembers member) {
    if (this.members == null) {
      this.members = new HashSet<>();
    }
    Long existingUsersWithId =
        this.members.stream()
            .filter(
                existingMember -> existingMember.getUser().getId().equals(member.getUser().getId()))
            .count();
    if (existingUsersWithId > 0) {
      return;
    }
    this.members.add(member);
  }

  public void addMembers(Collection<FamilyMembers> members) {
    if (this.members == null) {
      this.members = new HashSet<>();
    }
    this.members.addAll(members);
  }

  public Optional<FamilyMembers> getOwner() {
    return this.members.stream().filter(member -> Role.OWNER.equals(member.getRole())).findFirst();
  }

  public Set<Calendar> getCalendars() {
    return calendars;
  }

  public void setCalendars(Set<Calendar> calendars) {
    this.calendars = calendars;
  }

  public void addCalendar(Calendar calendar) {
    if (calendars == null) {
      calendars = new HashSet<>();
    }
    this.calendars.add(calendar);
  }

  public Set<ShoppingList> getShoppingLists() {
    return shoppingLists;
  }

  public void setShoppingLists(Set<ShoppingList> shoppingLists) {
    this.shoppingLists = shoppingLists;
  }

  public void addShoppingList(ShoppingList list) {
    if (shoppingLists == null) {
      shoppingLists = new HashSet<>();
    }
    this.shoppingLists.add(list);
  }

  public Set<ToDoList> getToDoLists() {
    return toDoLists;
  }

  public void setToDoLists(Set<ToDoList> toDoLists) {
    this.toDoLists = toDoLists;
  }

  public void addToDoList(ToDoList toDoList) {
    if (toDoLists == null) {
      toDoLists = new HashSet<>();
    }
    this.toDoLists.add(toDoList);
  }

  public boolean isMember(User user) {
    return this.members.stream()
            .filter(member -> member.getUser().getId().equals(user.getId()))
            .count()
        > 0;
  }

  @Override
  public String toString() {
    return "Family [id="
        + id
        + ", name="
        + name
        + ", eventColor="
        + eventColor
        + ", timezone="
        + timezone
        + ", inviteCode="
        + inviteCode
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventColor, id, inviteCode, name, timezone);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Family other = (Family) obj;
    return Objects.equals(eventColor, other.eventColor)
        && Objects.equals(id, other.id)
        && Objects.equals(inviteCode, other.inviteCode)
        && Objects.equals(name, other.name)
        && Objects.equals(timezone, other.timezone);
  }
}
