package com.familyorg.familyorganizationapp.repository.custom;

import com.familyorg.familyorganizationapp.domain.ShoppingList;
import java.util.List;

public interface ShoppingListRepositoryCustom {
  List<ShoppingList> getFilteredPolls(List<Long> familyIdFilters, List<Long> listFilters);
}
