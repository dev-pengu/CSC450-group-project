package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.QToDoList;
import com.familyorg.familyorganizationapp.domain.QToDoTask;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.ToDoList;
import com.familyorg.familyorganizationapp.repository.custom.ToDoListRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class ToDoListRepositoryImpl extends QuerydslRepositorySupport
    implements ToDoListRepositoryCustom {

  private Logger logger = LoggerFactory.getLogger(ToDoListRepositoryImpl.class);
  private QToDoList listTable = QToDoList.toDoList;
  private QToDoTask taskTable = QToDoTask.toDoTask;

  public ToDoListRepositoryImpl() {
    super(ToDoList.class);
  }

  @Override
  public List<ToDoList> getFilteredLists(List<Long> familyIds,
    List<Long> toDoListIds) {
    Objects.requireNonNull(familyIds);
    Objects.requireNonNull(toDoListIds);
    JPQLQuery<ToDoList> lists = from(listTable)
      .where(listTable.family.id.in(familyIds));
    if (!toDoListIds.isEmpty()) {
      lists.where(listTable.id.in(toDoListIds));
    }
    return lists.fetch();
  }

}
