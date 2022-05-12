package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.Date;

public class ToDoTaskDtoBuilder implements DtoBuilder<ToDoTaskDto> {
  private Long id;
  private String description;
  private String notes;
  private Boolean completed;
  private Date dueDate;
  private Date completedDateTime;
  private UserDto addedBy;
  private String created;
  private Long listId;

  public ToDoTaskDtoBuilder setId(Long id) {
    this.id = id;
    return this;
  }

  public ToDoTaskDtoBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public ToDoTaskDtoBuilder setCompleted(Boolean completed) {
    this.completed = completed;
    return this;
  }

  public ToDoTaskDtoBuilder setNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public ToDoTaskDtoBuilder setDueDate(Date dueDate) {
    this.dueDate = dueDate;
    return this;
  }

  public ToDoTaskDtoBuilder setCompletedDateTime(Date completedDateTime) {
    this.completedDateTime = completedDateTime;
    return this;
  }

  public ToDoTaskDtoBuilder setAddedBy(UserDto addedBy) {
    this.addedBy = addedBy;
    return this;
  }

  public ToDoTaskDtoBuilder setAddedBy(User addedBy) {
    this.addedBy = UserDto.fromUserObj(addedBy);
    return this;
  }

  public ToDoTaskDtoBuilder setCreated(String created) {
    this.created = created;
    return this;
  }

  public ToDoTaskDtoBuilder setListId(Long listId) {
    this.listId = listId;
    return this;
  }

  @Override
  public ToDoTaskDto build() {
    return new ToDoTaskDto(
        id, description, notes, completed, dueDate, completedDateTime, addedBy, created, listId);
  }
}
