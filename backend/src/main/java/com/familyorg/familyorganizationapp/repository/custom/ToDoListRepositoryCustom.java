package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import java.util.Set;
import com.familyorg.familyorganizationapp.domain.ToDoList;

public interface ToDoListRepositoryCustom {

  List<ToDoList> getFilteredLists(List<Long> familyIds, List<Long> todolistIds);
}
