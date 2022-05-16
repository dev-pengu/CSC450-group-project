package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import java.util.Objects;

public class ToDoTaskDto {
  private final Long id;
  private final String description;
  private final String notes;
  private final Boolean completed;
  private final Date dueDate;
  private final Date completedDateTime;
  private final UserDto addedBy;
  private final String created;
  private Boolean dueNextTwoDays;
  private final Boolean overdue;

  @JsonInclude(Include.NON_NULL)
  private final Long listId;

  public ToDoTaskDto(
      Long id,
      String description,
      String notes,
      Boolean completed,
      Date dueDate,
      Date completedDateTime,
      UserDto addedBy,
      String created,
      Long listId,
      Boolean overdue,
      Boolean dueNextTwoDays) {
    this.id = id;
    this.description = description == null ? null : description.trim();
    this.notes = notes == null ? null : notes.trim();
    this.completed = completed;
    this.dueDate = dueDate;
    this.completedDateTime = completedDateTime;
    this.addedBy = addedBy;
    this.created = created;
    this.listId = listId;
    this.overdue = overdue;
    this.dueNextTwoDays = dueNextTwoDays;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getNotes() {
    return notes;
  }

  public Boolean isCompleted() {
    return completed;
  }

  public Date getDueDate() {
    return dueDate;
  }

  public Date getCompletedDateTime() {
    return completedDateTime;
  }

  public UserDto getAddedBy() {
    return addedBy;
  }

  public String getCreated() {
    return created;
  }

  public Long getListId() {
    return listId;
  }

  public Boolean isOverdue() {
    return overdue;
  }

  public Boolean isDueNextTwoDays() {
    return dueNextTwoDays;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ToDoTaskDto that = (ToDoTaskDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(description, that.description)
        && Objects.equals(notes, that.notes)
        && Objects.equals(completed, that.completed)
        && Objects.equals(dueDate, that.dueDate)
        && Objects.equals(completedDateTime, that.completedDateTime)
        && Objects.equals(addedBy, that.addedBy)
        && Objects.equals(created, that.created)
        && Objects.equals(listId, that.listId)
        && Objects.equals(overdue, that.overdue)
        && Objects.equals(dueNextTwoDays, that.dueNextTwoDays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        description,
        notes,
        completed,
        dueDate,
        completedDateTime,
        addedBy,
        created,
        listId,
        overdue,
        dueNextTwoDays);
  }

  @Override
  public String toString() {
    return "ToDoTaskDto{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", notes='"
        + notes
        + '\''
        + ", completed="
        + completed
        + ", dueDateTime='"
        + dueDate
        + '\''
        + ", completedDateTime='"
        + completedDateTime
        + '\''
        + ", addedBy="
        + addedBy
        + ", created='"
        + created
        + '\''
        + ", listId="
        + listId
        + ", overdue="
        + overdue
        + ", dueNextTwoDays="
        + dueNextTwoDays
        + '}';
  }
}
