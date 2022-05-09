package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.QShoppingList;
import com.familyorg.familyorganizationapp.domain.ShoppingList;
import com.familyorg.familyorganizationapp.repository.ShoppingListItemRepository;
import com.familyorg.familyorganizationapp.repository.custom.ShoppingListRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ShoppingListRepositoryImpl extends QuerydslRepositorySupport implements
  ShoppingListRepositoryCustom {
  QShoppingList listTable = QShoppingList.shoppingList;

  public ShoppingListRepositoryImpl() {
    super(ShoppingList.class);
  }

  @Override
  public List<ShoppingList> getFilteredPolls(List<Long> familyIdFilters,
    List<Long> listFilters) {
    JPQLQuery<ShoppingList> lists = from(listTable)
      .where(listTable.family.id.in(familyIdFilters));
    if (!listFilters.isEmpty()) {
      lists.where(listTable.id.in(listFilters));
    }
    return lists.fetch();
  }
}
