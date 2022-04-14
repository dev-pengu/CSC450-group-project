package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import java.util.Set;
import com.familyorg.familyorganizationapp.domain.Todolist;

public interface TodolistRepositoryCustom {

  List<Todolist> todolistDataByFamilyIds(List<Long> familyIds);

  List<Todolist> search(List<Long> familyIds, List<Long> todolistIds, 
		  List<Long> todotaskIds, Set<Long> userIds);
}