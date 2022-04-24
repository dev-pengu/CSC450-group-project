package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;

public class TodotaskDto
{
	private Long id;
	private String description;
	private Boolean completed;

	public TodotaskDto(Long id, String description, Boolean completed) {
		super();
		this.id = id;
		this.description = description;
		this.completed = completed;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public Boolean getCompleted() {
		return completed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(completed, description, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TodotaskDto other = (TodotaskDto) obj;
		return Objects.equals(completed, other.completed) && Objects.equals(description, other.description)
				&& Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "TodotaskDto [id=" + id + ", description=" + description + ", completed="
				+ completed + "]";
	}
	
	
	
}
