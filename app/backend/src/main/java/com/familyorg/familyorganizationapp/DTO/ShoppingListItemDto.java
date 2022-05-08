package com.familyorg.familyorganizationapp.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Objects;

public class ShoppingListItemDto {
  private final Long id;
  private final String description;
  private final Integer amount;
  private final String unit;
  private final String notes;
  private final UserDto addedBy;

  @JsonInclude(Include.NON_NULL)
  private final Long listId;

  public ShoppingListItemDto(
      Long id,
      String description,
      Integer amount,
      String unit,
      String notes,
      Long listId,
      UserDto addedBy) {
    this.id = id;
    this.description = description == null ? null : description.trim();
    this.amount = amount;
    this.unit = unit == null ? null : unit.trim();
    this.notes = notes == notes ? null : notes.trim();
    this.listId = listId;
    this.addedBy = addedBy;
  }

  public Long getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public Integer getAmount() {
    return amount;
  }

  public String getUnit() {
    return unit;
  }

  public String getNotes() {
    return notes;
  }

  public Long getListId() {
    return listId;
  }

  public UserDto getAddedBy() {
    return addedBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShoppingListItemDto that = (ShoppingListItemDto) o;
    return Objects.equals(id, that.id)
        && Objects.equals(description, that.description)
        && Objects.equals(amount, that.amount)
        && Objects.equals(unit, that.unit)
        && Objects.equals(notes, that.notes)
        && Objects.equals(listId, that.listId)
        && Objects.equals(addedBy, that.addedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, amount, unit, notes, listId, addedBy);
  }

  @Override
  public String toString() {
    return "ShoppingListItemDto{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", amount="
        + amount
        + ", unit='"
        + unit
        + '\''
        + ", notes='"
        + notes
        + '\''
        + ", listId="
        + listId
        + ", addedBy="
        + addedBy
        + '}';
  }
}
