package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.TodolistDto;
import com.familyorg.familyorganizationapp.DTO.TodotaskDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.List;

public class ToDoTaskDtoBuilder implements DtoBuilder<TodotaskDto> {
	  private Long id;
	  private String description;
	  private Boolean completed;
	  
	  @Override
	  public TodotaskDto build() {
	    return new TodotaskDto(
	        id, description, completed);
	  }

	public ToDoTaskDtoBuilder setId(Long id) {
		this.id = id;
		return this;
	}

	public ToDoTaskDtoBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public ToDoTaskDtoBuilder setCompleted(Boolean completed) {
		this.completed = completed;
		return this;
	} 
	  
}