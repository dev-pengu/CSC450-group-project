package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.ShoppingListDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchResponseDto;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.ShoppingList;

public interface ShoppingListService {
  void createShoppingList(ShoppingListDto request);

  void updateShoppingList(ShoppingListDto request);

  void deleteShoppingList(Long id);

  ShoppingListDto getShoppingList(Long id);

  void addItem(ShoppingListItemDto request);

  void updateItem(ShoppingListItemDto request);

  void deleteItem(Long id);

  ShoppingListItemDto getItem(Long id);

  ShoppingListSearchResponseDto search(ShoppingListSearchRequestDto request);
}
