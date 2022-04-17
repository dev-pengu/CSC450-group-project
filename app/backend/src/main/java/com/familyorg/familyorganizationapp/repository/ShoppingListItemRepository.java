package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.domain.ShoppingListItem;
import com.familyorg.familyorganizationapp.repository.custom.ShoppingListItemRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingListItemRepository extends CrudRepository<ShoppingListItem, Long>,
  ShoppingListItemRepositoryCustom {

}
