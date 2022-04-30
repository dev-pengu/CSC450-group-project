package com.familyorg.familyorganizationapp.repository.custom;

import com.familyorg.familyorganizationapp.domain.ToDoTask;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface ToDoTaskRepositoryCustom {
  public Map<Long, List<ToDoTask>> getFilteredTasksByList(
      List<Long> listIds, Date start, Date end, Boolean completed);
}
