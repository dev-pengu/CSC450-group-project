package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.repository.custom.ToDoTaskRepositoryCustom;
import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.ToDoTask;

public interface ToDoTaskRepository
    extends CrudRepository<ToDoTask, Long>, ToDoTaskRepositoryCustom {
  // Spring data methods only
}
