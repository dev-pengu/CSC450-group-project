package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.Todotask;
import com.familyorg.familyorganizationapp.repository.custom.TodolistRepositoryCustom;

public interface TodotaskRepository
    extends CrudRepository<Todotask, Long>, TodolistRepositoryCustom {
  // Spring data methods only
}