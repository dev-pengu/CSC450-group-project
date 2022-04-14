package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.familyorg.familyorganizationapp.domain.Todotask;
import com.familyorg.familyorganizationapp.domain.Todolist;

public interface TodotasktRepositoryImpl 
{
  Map<Long, List<Todotask>> getEventsByTodolistIdsInDateRange(Set<Long> todolistIds,
      Set<Long> todotaskIds, Set<Long> userIds);
}