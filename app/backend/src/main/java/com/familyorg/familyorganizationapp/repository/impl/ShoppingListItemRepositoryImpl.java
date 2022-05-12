package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.ShoppingListItem;
import com.familyorg.familyorganizationapp.repository.custom.ShoppingListItemRepositoryCustom;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ShoppingListItemRepositoryImpl extends QuerydslRepositorySupport implements
  ShoppingListItemRepositoryCustom {

  public ShoppingListItemRepositoryImpl() {
    super(ShoppingListItem.class);
  }

}
