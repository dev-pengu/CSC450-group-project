package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;

public class TodolistDto
{
	private Long id;
	private String description;
	private Boolean defaultList;
	private Long familyId;
	private List<TodotaskDto> tasks;
	
	
	public TodolistDto() {
		super();
	}
	
	public TodolistDto(Long id) {
		super();
		this.id = id;
	}
	
	public TodolistDto(Long id, String description) {
		super();
		this.id = id;
		this.description = description;
	}

	public TodolistDto(Long id, String description, Boolean defaultList) {
		super();
		this.id = id;
		this.description = description;
		this.defaultList = defaultList;
	}

	public TodolistDto(Long id, String description, Boolean defaultList, Long familyId) {
		super();
		this.id = id;
		this.description = description;
		this.defaultList = defaultList;
		this.familyId = familyId;
	}

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
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getDefaultList() {
		return defaultList;
	}
	public void setDefaultList(Boolean defaultList) {
		this.defaultList = defaultList;
	}
	public Long getFamilyId() {
		return familyId;
	}
	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}
	public List<TodotaskDto> getTasks() {
		return tasks;
	}
	public void setTasks(List<TodotaskDto> tasks) {
		this.tasks = tasks;
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
				&& Objects.equals(familyId, other.familyId) && Objects.equals(id, other.id)
				&& Objects.equals(tasks, other.tasks);
	}
	@Override
	public int hashCode() {
		return Objects.hash(defaultList, description, familyId, id, tasks);
	}
	@Override
	public String toString() {
		return "TodolistDto [id=" + id + ", description=" + description + ", defaultList=" + defaultList + ", familyId="
				+ familyId + ", tasks=" + tasks + "]";
	}
	
}