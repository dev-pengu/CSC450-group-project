package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "todo_task")
public class ToDoTask implements Serializable {

  private static final long serialVersionUID = -7734277033641279270L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "todo_task_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "due_date", columnDefinition = "DATE")
  private Date dueDate;

  @Column(name = "completed_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp completedDatetime;

  @Column(name = "description", columnDefinition = "VARCHAR(50)")
  private String description;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @Column(name = "created_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp createdDatetime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User addedBy;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "todo_list_id",
      referencedColumnName = "todo_list_id",
      columnDefinition = "BIGINT")
  private ToDoList list;

  public ToDoTask() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public void setDueDate(Date dueDate) {
    this.dueDate = dueDate;
  }

  public Timestamp getCompletedDatetime() {
    return completedDatetime;
  }

  public void setCompletedDatetime(Timestamp completedDatetime) {
    this.completedDatetime = completedDatetime;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Timestamp getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Timestamp createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public User getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(User addedBy) {
    this.addedBy = addedBy;
  }

  public ToDoList getList() {
    return list;
  }

  public void setList(ToDoList list) {
    this.list = list;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ToDoTask toDoTask = (ToDoTask) o;
    return id.equals(toDoTask.id)
        && Objects.equals(dueDate, toDoTask.dueDate)
        && Objects.equals(completedDatetime, toDoTask.completedDatetime)
        && Objects.equals(description, toDoTask.description)
        && Objects.equals(notes, toDoTask.notes)
        && Objects.equals(createdDatetime, toDoTask.createdDatetime)
        && Objects.equals(addedBy, toDoTask.addedBy)
        && Objects.equals(list.getId(), toDoTask.list.getId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        dueDate,
        completedDatetime,
        description,
        notes,
        createdDatetime,
        addedBy,
        list.getId());
  }

  @Override
  public String toString() {
    return "ToDoTask{"
        + "id="
        + id
        + ", dueDatetime="
        + dueDate
        + ", completedDatetime="
        + completedDatetime
        + ", description='"
        + description
        + '\''
        + ", notes='"
        + notes
        + '\''
        + ", createdDatetime="
        + createdDatetime
        + ", addedBy="
        + addedBy
        + ", list="
        + list.getId()
        + '}';
  }
}
