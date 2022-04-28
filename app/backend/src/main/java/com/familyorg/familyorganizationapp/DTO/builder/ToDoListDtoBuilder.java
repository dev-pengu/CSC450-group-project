package com.familyorg.familyorganizationapp.DTO.builder;

import com.familyorg.familyorganizationapp.DTO.TodolistDto;
import com.familyorg.familyorganizationapp.DTO.TodotaskDto;
import com.familyorg.familyorganizationapp.DTO.UserDto;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.List;

public class ToDoListDtoBuilder implements DtoBuilder<TodolistDto> {
	  private Long id;
	  private Long familyId;
	  private String description;
	  private Boolean isDefault;
	  private List<TodotaskDto> tasks;
	  
	  @Override
	  public TodolistDto build() {
	    return new TodolistDto(
	        id, description, isDefault, familyId, tasks);
	  }

	public ToDoListDtoBuilder setId(Long id) {
		this.id = id;
		return this;
	}

	public ToDoListDtoBuilder setFamilyId(Long familyId) {
		this.familyId = familyId;
		return this;
	}

	public ToDoListDtoBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public ToDoListDtoBuilder setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
		return this;
	}

	public ToDoListDtoBuilder setTasks(List<TodotaskDto> tasks) {
		this.tasks = tasks;
		return this;
	}
	  
	  
}