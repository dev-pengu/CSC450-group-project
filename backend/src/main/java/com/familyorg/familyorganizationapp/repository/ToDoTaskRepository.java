package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.repository.custom.ToDoTaskRepositoryCustom;
import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.ToDoTask;
import com.familyorg.familyorganizationapp.repository.custom.ToDoListRepositoryCustom;

public interface ToDoTaskRepository
    extends CrudRepository<ToDoTask, Long>, ToDoTaskRepositoryCustom {
  // Spring data methods only
}
