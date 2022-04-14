package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.Todolist;
import com.familyorg.familyorganizationapp.repository.custom.TodolistRepositoryCustom;

public interface TodolistRepository
    extends CrudRepository<Todolist, Long>, TodolistRepositoryCustom {
  // Spring data methods only
}