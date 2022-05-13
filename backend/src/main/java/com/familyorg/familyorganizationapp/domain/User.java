package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User implements Serializable {

  private static final long serialVersionUID = -4364743442691665173L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "first_name", columnDefinition = "VARCHAR(50)", nullable = false)
  private String firstName;

  @Column(name = "last_name", columnDefinition = "VARCHAR(50)", nullable = false)
  private String lastName;

  @Column(name = "username", columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
  private String username;

  @Column(name = "password", columnDefinition = "VARCHAR(64)", nullable = false)
  private String password;

  @Column(name = "email", columnDefinition = "VARCHAR(70)", nullable = false, unique = true)
  private String email;

  @Column(name = "timezone", columnDefinition = "VARCHAR(256)", nullable = true)
  private String timezone;

  @Column(name = "dark_mode", columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean darkMode;

  @Column(name = "last_logged_in", columnDefinition = "TIMESTAMP")
  private Timestamp lastLoggedIn;

  @JsonIgnore
  @Column(name = "is_locked", columnDefinition = "BOOLEAN DEFAULT FALSE")
  private Boolean locked;

  @JsonIgnore
  @Column(name = "login_attempts", columnDefinition = "INTEGER DEFAULT 0")
  private Integer loginAttempts;

  @JsonIgnore
  @OneToMany(
      mappedBy = "user",
      fetch = FetchType.LAZY,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private Set<FamilyMembers> families;

  @ManyToMany(mappedBy = "assignees", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  Set<CalendarEvent> events = new HashSet<CalendarEvent>();

  @JsonIgnore
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
  Set<PasswordResetCode> resetCodes;

  public User() {
  }

  public User(
      Long id,
      String firstName,
      String lastName,
      String username,
      String password,
      String email,
      Set<FamilyMembers> families) {
    this(id, firstName, lastName, username, password, email, null, families);
  }

  public User(
      Long id,
      String firstName,
      String lastName,
      String username,
      String password,
      String email,
      String timezone,
      Set<FamilyMembers> families) {
    Objects.requireNonNull(firstName);
    Objects.requireNonNull(lastName);
    Objects.requireNonNull(username);
    Objects.requireNonNull(password);
    Objects.requireNonNull(email);
    this.id = id;
    this.firstName = firstName.trim();
    this.lastName = lastName.trim();
    this.username = username.trim().toLowerCase();
    this.password = password.trim();
    this.email = email.trim().toLowerCase();
    this.families = families;
    this.timezone = timezone == null ? null : timezone.trim();
    this.darkMode = false;
    this.loginAttempts = 0;
    this.locked = false;
  }

  public User(
      String firstName,
      String lastName,
      String username,
      String password,
      String email,
      Set<FamilyMembers> families) {
    this(firstName, lastName, username, password, email, null, families);
  }

  public User(
      String firstName,
      String lastName,
      String username,
      String password,
      String email,
      String timezone,
      Set<FamilyMembers> families) {
    Objects.requireNonNull(firstName);
    Objects.requireNonNull(lastName);
    Objects.requireNonNull(username);
    Objects.requireNonNull(password);
    Objects.requireNonNull(email);
    this.firstName = firstName.trim();
    this.lastName = lastName.trim();
    this.username = username.trim().toLowerCase();
    this.password = password.trim();
    this.email = email.trim().toLowerCase();
    this.families = families;
    this.timezone = timezone == null ? null : timezone.trim();
    this.darkMode = false;
    this.loginAttempts = 0;
    this.locked = false;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    Objects.requireNonNull(firstName);
    this.firstName = firstName.trim();
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    Objects.requireNonNull(lastName);
    this.lastName = lastName.trim();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    Objects.requireNonNull(username);
    this.username = username.trim().toLowerCase();
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    Objects.requireNonNull(password);
    this.password = password.trim();
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    Objects.requireNonNull(email);
    this.email = email.trim().toLowerCase();
  }

  public Set<FamilyMembers> getFamilies() {
    return families;
  }

  public void setFamilies(Set<FamilyMembers> families) {
    this.families = families;
  }

  public String getFullname() {
    return this.firstName + " " + this.lastName;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public void setDarkMode(Boolean useDarkMode) {
    this.darkMode = useDarkMode;
  }

  public Boolean useDarkMode() {
    return this.darkMode;
  }

  public Timestamp getLastLoggedIn() {
    return lastLoggedIn;
  }

  public void setLastLoggedIn(Timestamp lastLoggedIn) {
    this.lastLoggedIn = lastLoggedIn;
  }

  public Set<CalendarEvent> getEvents() {
    return events;
  }

  public void setEvents(Set<CalendarEvent> events) {
    this.events = events;
  }

  public Set<PasswordResetCode> getResetCodes() {
    return resetCodes;
  }

  public void setResetCodes(Set<PasswordResetCode> resetCodes) {
    this.resetCodes = resetCodes;
  }

  public Boolean isLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  public Integer getLoginAttempts() {
    return loginAttempts;
  }

  public void setLoginAttempts(Integer attempts) {
    this.loginAttempts = attempts;
  }

  public void addLoginAttempt() {
    this.loginAttempts += 1;
  }

  @Override
  public String toString() {
    return "User [id="
        + id
        + ", firstName="
        + firstName
        + ", lastName="
        + lastName
        + ", username="
        + username
        + ", email="
        + email
        + ", timezone="
        + timezone
        + ", isLocked="
        + locked
        + ", loginAttempts="
        + loginAttempts
        + "]";
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, firstName, id, lastName, username);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    User other = (User) obj;
    return Objects.equals(email, other.email)
        && Objects.equals(firstName, other.firstName)
        && Objects.equals(id, other.id)
        && Objects.equals(lastName, other.lastName)
        && Objects.equals(password, other.password)
        && Objects.equals(username, other.username);
  }

  public boolean isValid() {
    if (firstName == null
        || lastName == null
        || username == null
        || email == null
        || password == null) {
      return false;
    }

    return true;
  }
}
