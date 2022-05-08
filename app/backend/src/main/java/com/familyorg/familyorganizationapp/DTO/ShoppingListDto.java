package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;

public class ShoppingListDto {
  private final Long id;
  private final String description;
  private final String created;
  private final Boolean isDefault;
  private final Long familyId;
  private final UserDto createdBy;
  private final List<ShoppingListItemDto> items;
  private final String color;

  public ShoppingListDto(
      Long id,
      String description,
      String created,
      Boolean isDefault,
      Long familyId,
      UserDto createdBy,
      List<ShoppingListItemDto> items,
      String color) {
    this.id = id;
    this.description = description == null ? null : description.trim();
    this.created = created;
    this.isDefault = isDefault;
    this.familyId = familyId;
    this.createdBy = createdBy;
    this.items = items;
    this.color = color == null ? null : color.trim();
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public String getCreated() {
    return created;
  }

  public Boolean getDefault() {
    return isDefault;
  }

  public Long getFamilyId() {
    return familyId;
  }

  public UserDto getCreatedBy() {
    return createdBy;
  }

  public List<ShoppingListItemDto> getItems() {
    return items;
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
    ShoppingListDto that = (ShoppingListDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(description, that.description)
        && Objects.equals(created, that.created)
        && Objects.equals(isDefault, that.isDefault)
        && Objects.equals(familyId, that.familyId)
        && Objects.equals(createdBy, that.createdBy)
        && Objects.equals(items, that.items)
        && Objects.equals(color, that.color);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, created, isDefault, familyId, createdBy, items, color);
  }

  @Override
  public String toString() {
    return "ShoppingListDto{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", created='"
        + created
        + '\''
        + ", isDefault="
        + isDefault
        + ", familyId="
        + familyId
        + ", createdBy="
        + createdBy
        + ", items="
        + items
        + ", color='"
        + color
        + '\''
        + '}';
  }
}
