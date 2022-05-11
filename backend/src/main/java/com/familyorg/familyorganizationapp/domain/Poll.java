package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "poll")
public class Poll implements Serializable {

  private static final long serialVersionUID = -8450640631480007395L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "family_id", referencedColumnName = "family_id", columnDefinition = "BIGINT")
  private Family family;

  @Column(name = "description", columnDefinition = "VARCHAR(256)", nullable = false)
  private String description;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "created_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp createdDatetime;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User createdBy;

  @Column(name = "close_datetime", columnDefinition = "TIMESTAMP", nullable = false)
  private Timestamp closeDateTime;

  @Column(name = "timezone", columnDefinition = "VARCHAR(256)")
  private String timezone;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "poll_id")
  private List<PollOption> options;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "poll_id")
  private Set<PollVote> respondents;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Family getFamily() {
    return family;
  }

  public void setFamily(Family family) {
    this.family = family;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    Objects.requireNonNull(description);
    this.description = description.trim();
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes == null ? null : notes.trim();
  }

  public Timestamp getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Timestamp createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public Timestamp getCloseDateTime() {
    return closeDateTime;
  }

  public void setCloseDateTime(Timestamp closeDateTime) {
    this.closeDateTime = closeDateTime;
  }

  public List<PollOption> getOptions() {
    return options;
  }

  public void setOptions(List<PollOption> options) {
    this.options = options;
  }

  public void addOption(PollOption option) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(option);
  }

  public String getTimezone() {
    return this.timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Set<PollVote> getRespondents() {
    return respondents;
  }

  public void setRespondents(Set<PollVote> respondents) {
    this.respondents = respondents;
  }

  @Override
  public int hashCode() {
    return Objects.hash(closeDateTime, createdDatetime, description, id, notes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Poll other = (Poll) obj;
    return Objects.equals(closeDateTime, other.closeDateTime)
        && Objects.equals(createdDatetime, other.createdDatetime)
        && Objects.equals(description, other.description)
        && Objects.equals(id, other.id)
        && Objects.equals(notes, other.notes);
  }

  @Override
  public String toString() {
    return "Poll [id="
        + id
        + ", description="
        + description
        + ", notes="
        + notes
        + ", createdDatetime="
        + createdDatetime
        + ", closeDateTime="
        + closeDateTime
        + ", options="
        + options
        + ",timezone="
        + timezone
        + "]";
  }
}
