package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.ToDoList;
import com.familyorg.familyorganizationapp.repository.custom.ToDoListRepositoryCustom;

public interface ToDoListRepository
    extends CrudRepository<ToDoList, Long>, ToDoListRepositoryCustom {
  // Spring data methods only
}
