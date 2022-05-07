package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.QToDoTask;
import com.familyorg.familyorganizationapp.domain.ToDoTask;
import com.familyorg.familyorganizationapp.repository.custom.ToDoTaskRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class ToDoTaskRepositoryImpl extends QuerydslRepositorySupport
    implements ToDoTaskRepositoryCustom {
  private QToDoTask taskTable = QToDoTask.toDoTask;

  public ToDoTaskRepositoryImpl() {
    super(ToDoTask.class);
  }

  @Override
  public Map<Long, List<ToDoTask>> getFilteredTasksByList(
      List<Long> listIds, Date start, Date end, Boolean completed) {
    Map<Long, List<ToDoTask>> result = new HashMap<>();
    JPQLQuery<ToDoTask> query = from(taskTable).where(taskTable.list.id.in(listIds));

    if (start != null && end != null) {
      query.where(taskTable.dueDate.between(start, end));
    } else if (start != null) {
      query.where(taskTable.dueDate.goe(start));
    } else if (end != null) {
      query.where(taskTable.dueDate.loe(end));
    }

    if (completed != null && completed) {
      query.where(taskTable.completedDatetime.isNotNull());
    } else if (completed != null) {
      query.where(taskTable.completedDatetime.isNull());
    }

    List<ToDoTask> tasks = query.fetch();

    for (ToDoTask task : tasks) {
      if (result.containsKey(task.getList().getId())) {
        result.get(task.getList().getId()).add(task);
      } else {
        result.put(
            task.getList().getId(),
            new ArrayList<>() {
              {
                add(task);
              }
            });
      }
    }

    return result;
  }
}
