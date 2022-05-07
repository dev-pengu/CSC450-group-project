package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;

public class ToDoListDto {
  private Long id;
  private String description;
  private Boolean isDefault;
  private Long familyId;
  private List<ToDoTaskDto> tasks;
  private UserDto createdBy;
  private String created;
  private String color;

  public ToDoListDto(
      Long id,
      String description,
      Boolean isDefault,
      Long familyId,
      List<ToDoTaskDto> tasks,
      UserDto createdBy,
      String created,
      String color) {
    this.id = id;
    this.description = description;
    this.isDefault = isDefault;
    this.familyId = familyId;
    this.tasks = tasks;
    this.createdBy = createdBy;
    this.created = created;
    this.color = color;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public List<ToDoTaskDto> getTasks() {
    return tasks;
  }

  public Boolean getDefault() {
    return isDefault;
  }

  public UserDto getCreatedBy() {
    return createdBy;
  }

  public String getCreated() {
    return created;
  }

  public String getColor() {
    return color;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ToDoListDto that = (ToDoListDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(description, that.description)
        && Objects.equals(isDefault, that.isDefault)
        && Objects.equals(familyId, that.familyId)
        && Objects.equals(createdBy, that.createdBy)
        && Objects.equals(created, that.created)
        && Objects.equals(color, that.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, isDefault, familyId, createdBy, created, color);
  }

  @Override
  public String toString() {
    return "ToDoListDto{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", isDefault="
        + isDefault
        + ", familyId="
        + familyId
        + ", tasks="
        + tasks
        + ", createdBy="
        + createdBy
        + ", created='"
        + created
        + '\''
        + ", color='"
        + color
        + '\''
        + '}';
  }
}
