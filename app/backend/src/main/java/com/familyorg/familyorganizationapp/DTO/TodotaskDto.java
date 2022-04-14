package com.familyorg.familyorganizationapp.DTO;

import java.util.List;
import java.util.Objects;

public class TodotaskDto
{
	private Long id;
	private Long familyId;
	private String description;
	private Boolean completed;
	
	public TodotaskDto() {
		super();
	}
	
	public TodotaskDto(Long id, Long familyId) {
		super();
		this.id = id;
		this.familyId = familyId;
	}

	public TodotaskDto(Long id, Long familyId, String description) {
		super();
		this.id = id;
		this.familyId = familyId;
		this.description = description;
	}

	public TodotaskDto(Long id, Long familyId, String description, Boolean completed) {
		super();
		this.id = id;
		this.familyId = familyId;
		this.description = description;
		this.completed = completed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFamilyId() {
		return familyId;
	}

	public void setFamilyId(Long familyId) {
		this.familyId = familyId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(completed, description, familyId, id);
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
				&& Objects.equals(familyId, other.familyId) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "TodotaskDto [id=" + id + ", familyId=" + familyId + ", description=" + description + ", completed="
				+ completed + "]";
	}
	
	
	
}
