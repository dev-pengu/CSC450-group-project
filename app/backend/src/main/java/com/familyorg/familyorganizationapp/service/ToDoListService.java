package com.familyorg.familyorganizationapp.service;

import com.familyorg.familyorganizationapp.DTO.ToDoListDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;

public interface ToDoListService {

  //List CRUD

  void createList(ToDoListDto request);

  void updateList(ToDoListDto request);

  void deleteList(Long id);

  ToDoListDto getToDoList(Long id);

  //Task CRUD

  ToDoTaskDto getTask(Long id);

  void addTask(ToDoTaskDto task);

  void updateTask(ToDoTaskDto task);

  void deleteTask(Long id);

  ToDoListSearchResponseDto search(ToDoListSearchRequestDto request);

}
