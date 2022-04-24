package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.TodolistDto;
import com.familyorg.familyorganizationapp.DTO.TodotaskDto;

public interface TodolistService {
	
  //List CRUD
  
  void createList(TodolistDto request);
  
  void updateList(TodolistDto request);
  
  void deleteList(Long id);
  
  //Task CRUD
  
  TodotaskDto getTask(Long id);
  
  void addTask(TodotaskDto task);

  void updateTask(TodotaskDto task);

  void deleteTask(Long id);

}
