package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
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
@Table(name = "calendar")
public class Calendar implements Serializable {

  private static final long serialVersionUID = 6058876845772310313L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "calendar_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "is_default", columnDefinition = "BOOLEAN")
  private Boolean isDefault;

  @Column(name = "description", columnDefinition = "VARCHAR(70)")
  private String description;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "family_id", referencedColumnName = "family_id",
      columnDefinition = "BIGINT")
  private Family family;

  @OneToMany(mappedBy = "calendar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<CalendarEvent> events;

  public Calendar() {}

  public Calendar(Family family, String description) {
    this.family = family;
    this.description = description;
    this.isDefault = true;
  }

  public Calendar(Family family, String description, boolean isDefault) {
    this.family = family;
    this.description = description;
    this.isDefault = isDefault;
  }

  public Calendar(Long id, String description, boolean isDefault, Family family,
      List<CalendarEvent> events) {
    super();
    this.id = id;
    this.description = description;
    this.isDefault = isDefault;
    this.family = family;
    this.events = events;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }

  public Family getFamily() {
    return family;
  }

  public void setFamily(Family family) {
    this.family = family;
  }

  public List<CalendarEvent> getEvents() {
    return events;
  }

  public void setEvents(List<CalendarEvent> events) {
    this.events = events;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, isDefault, description);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Calendar other = (Calendar) obj;
    return Objects.equals(id, other.id) && isDefault == other.isDefault
        && Objects.equals(description, other.description);
  }

  @Override
  public String toString() {
    return "Calendar [id=" + id + ", description=" + description + ", isDefault=" + isDefault
        + ", events=" + events + "]";
  }


}
