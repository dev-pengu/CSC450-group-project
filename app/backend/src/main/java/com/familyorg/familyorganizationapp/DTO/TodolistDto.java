package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.domain.Family;

public class TodolistDto
{
	private Long id;
	private String description;
	private Boolean defaultList;
	private Long familyId;
	private List<TodotaskDto> tasks;

	public TodolistDto(Long id, String description, Boolean defaultList, Long familyId, List<TodotaskDto> tasks) {
		super();
		this.id = id;
		this.description = description;
		this.defaultList = defaultList;
		this.familyId = familyId;
		this.tasks = tasks;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Boolean getDefaultList() {
		return defaultList;
	}
	
	public Long getFamilyId() {
		return familyId;
	}
	
	public List<TodotaskDto> getTasks() {
		return tasks;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodolistDto other = (TodolistDto) obj;
		return Objects.equals(defaultList, other.defaultList) && Objects.equals(description, other.description)
				&& Objects.equals(familyId, other.familyId) && Objects.equals(id, other.id);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(defaultList, description, familyId, id);
	}
	
	@Override
	public String toString() {
		return "TodolistDto [id=" + id + ", description=" + description + ", defaultList=" + defaultList + ", familyId="
				+ familyId + ", tasks=" + tasks + "]";
	}
	
}