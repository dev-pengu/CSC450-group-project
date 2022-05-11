package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.domain.ShoppingList;
import com.familyorg.familyorganizationapp.repository.custom.ShoppingListRepositoryCustom;
import org.springframework.data.repository.CrudRepository;

public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long>,
  ShoppingListRepositoryCustom {

}
