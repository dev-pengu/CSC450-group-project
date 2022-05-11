package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;

public class ShoppingListItemDtoBuilder implements DtoBuilder<ShoppingListItemDto> {
  private Long id;
  private String description;
  private Double amount;
  private String unit;
  private String notes;
  private UserDto addedBy;
  private Long listId;

  public ShoppingListItemDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public ShoppingListItemDtoBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ShoppingListItemDtoBuilder withAmount(Double amount) {
    this.amount = amount;
    return this;
  }

  public ShoppingListItemDtoBuilder withUnits(String units) {
    this.unit = units;
    return this;
  }

  public ShoppingListItemDtoBuilder withNotes(String notes) {
    this.notes = notes;
    return this;
  }

  public ShoppingListItemDtoBuilder setAddedBy(UserDto user) {
    this.addedBy = user;
    return this;
  }

  public ShoppingListItemDtoBuilder setAddedBy(User user) {
    this.addedBy = UserDto.fromUserObj(user);
    return this;
  }

  public ShoppingListItemDtoBuilder withListId(Long id) {
    this.listId = id;
    return this;
  }

  @Override
  public ShoppingListItemDto build() {
    return new ShoppingListItemDto(id, description, amount, unit, notes, listId, addedBy);
  }
}
