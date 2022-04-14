package com.familyorg.familyorganizationapp.domain;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table (name="todotask")

public class Todotask implements Serializable {


	private static final long serialVersionUID = -7734277033641279270L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "todolist_id", columnDefinition = "BIGSERIAL")
	private Long id;
	
	@JsonIgnore
	  @ManyToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "family_id", referencedColumnName = "family_id",
	      columnDefinition = "BIGINT")
	  private Family family;
	
	@Column(name= "is_default", columnDefinition = "BOOLEAN")
	private Boolean isDefault;

	public Todotask() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
		this.family = family;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, isDefault);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todotask other = (Todotask) obj;
		return Objects.equals(id, other.id) && Objects.equals(isDefault, other.isDefault);
	}

	@Override
	public String toString() {
		return "To do task [id=" + id + ", isDefault=" + isDefault + "]";
	}
	
}
