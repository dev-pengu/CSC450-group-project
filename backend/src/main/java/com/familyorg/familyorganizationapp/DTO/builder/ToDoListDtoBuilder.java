package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ToDoListDto;
import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.ArrayList;
import java.util.List;

public class ToDoListDtoBuilder implements DtoBuilder<ToDoListDto> {
  private Long id;
  private String description;
  private Boolean isDefault;
  private Long familyId;
  private List<ToDoTaskDto> tasks;
  private UserDto createdBy;
  private String created;
  private String color;

  public ToDoListDtoBuilder setId(Long id) {
    this.id = id;
    return this;
  }

  public ToDoListDtoBuilder setFamilyId(Long familyId) {
    this.familyId = familyId;
    return this;
  }

  public ToDoListDtoBuilder setDescription(String description) {
    this.description = description;
    return this;
  }

  public ToDoListDtoBuilder setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  public ToDoListDtoBuilder setTasks(List<ToDoTaskDto> tasks) {
    this.tasks = tasks;
    return this;
  }

  public ToDoListDtoBuilder addTask(ToDoTaskDto task) {
    if (this.tasks == null) {
      this.tasks = new ArrayList<>();
    }
    this.tasks.add(task);
    return this;
  }

  public ToDoListDtoBuilder setCreatedBy(UserDto user) {
    this.createdBy = user;
    return this;
  }

  public ToDoListDtoBuilder setCreatedBy(User user) {
    this.createdBy = UserDto.fromUserObj(user);
    return this;
  }

  public ToDoListDtoBuilder setCreated(String created) {
    this.created = created;
    return this;
  }

  public ToDoListDtoBuilder setColor(String color) {
    this.color = color;
    return this;
  }

  @Override
  public ToDoListDto build() {
    return new ToDoListDto(id, description, isDefault, familyId, tasks, createdBy, created, color);
  }
}
