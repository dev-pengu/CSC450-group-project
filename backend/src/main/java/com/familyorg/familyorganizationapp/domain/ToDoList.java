package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
@Table(name = "todo_list")
public class ToDoList implements Serializable {

  private static final long serialVersionUID = -7734277033641279270L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "todo_list_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "description", columnDefinition = "VARCHAR(50)")
  private String description;

  @Column(name = "is_default", columnDefinition = "BOOLEAN")
  private Boolean defaultList;

  @Column(name = "created_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp createdDatetime;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "family_id", referencedColumnName = "family_id", columnDefinition = "BIGINT")
  private Family family;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User createdBy;

  @OneToMany(mappedBy = "list", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<ToDoTask> items = new ArrayList<>();

  public ToDoList() {}

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
    this.description = description;
  }

  public Boolean isDefault() {
    return defaultList;
  }

  public void setDefaultList(Boolean isDefault) {
    this.defaultList = isDefault;
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

  public List<ToDoTask> getItems() {
    return items;
  }

  public void setItems(List<ToDoTask> items) {
    this.items = items;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, defaultList, description);
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
    ToDoList other = (ToDoList) obj;
    return Objects.equals(id, other.id) && Objects.equals(defaultList, other.defaultList);
  }

  @Override
  public String toString() {
    return "ToDoList{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", isDefault="
        + defaultList
        + ", createdDatetime="
        + createdDatetime
        + ", createdBy="
        + createdBy
        + ", items="
        + items
        + '}';
  }
}
