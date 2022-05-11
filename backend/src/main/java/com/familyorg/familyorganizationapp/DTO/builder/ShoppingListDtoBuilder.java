package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.ShoppingListDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.List;

public class ShoppingListDtoBuilder implements DtoBuilder<ShoppingListDto> {
  private Long id;
  private String description;
  private String created;
  private Boolean isDefault;
  private Long familyId;
  private UserDto createdBy;
  private List<ShoppingListItemDto> items;
  private String color;

  public ShoppingListDtoBuilder withId(Long id) {
    this.id = id;
    return this;
  }

  public ShoppingListDtoBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public ShoppingListDtoBuilder withCreated(String created) {
    this.created = created;
    return this;
  }

  public ShoppingListDtoBuilder setDefault(Boolean isDefault) {
    this.isDefault = isDefault;
    return this;
  }

  public ShoppingListDtoBuilder withFamilyId(Long familyId) {
    this.familyId = familyId;
    return this;
  }

  public ShoppingListDtoBuilder setCreatedBy(UserDto createdBy) {
    this.createdBy = createdBy;
    return this;
  }

  public ShoppingListDtoBuilder setCreatedBy(User createdBy) {
    this.createdBy = UserDto.fromUserObj(createdBy);
    return this;
  }

  public ShoppingListDtoBuilder setItems(List<ShoppingListItemDto> items) {
    this.items = items;
    return this;
  }

  public ShoppingListDtoBuilder withColor(String color) {
    this.color = color;
    return this;
  }

  @Override
  public ShoppingListDto build() {
    return new ShoppingListDto(
        id, description, created, isDefault, familyId, createdBy, items, color);
  }
}
