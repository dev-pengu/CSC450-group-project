package com.familyorg.familyorganizationapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "shopping_list")
public class ShoppingList implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shopping_list_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "description", columnDefinition = "VARCHAR(50)", nullable = false)
  private String description;

  @Column(name = "created_datetime", columnDefinition = "TIMESTAMP")
  private Timestamp createdDatetime;

  @Column(name = "is_default", columnDefinition = "BOOLEAN")
  private Boolean isDefault;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "family_id", referencedColumnName = "family_id", columnDefinition = "BIGINT")
  private Family family;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User createdBy;

  @OneToMany(mappedBy = "list", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<ShoppingListItem> items = new ArrayList<>();

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
    Objects.requireNonNull(description);
    this.description = description.trim();
  }

  public Timestamp getCreatedDatetime() {
    return createdDatetime;
  }

  public void setCreatedDatetime(Timestamp createdDatetime) {
    this.createdDatetime = createdDatetime;
  }

  public Boolean getDefault() {
    return isDefault;
  }

  public void setDefault(Boolean aDefault) {
    isDefault = aDefault;
  }

  public Family getFamily() {
    return family;
  }

  public void setFamily(Family family) {
    this.family = family;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public List<ShoppingListItem> getItems() {
    return items;
  }

  public void setItems(List<ShoppingListItem> items) {
    this.items = items;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShoppingList that = (ShoppingList) o;
    return id.equals(that.id)
        && description.equals(that.description)
        && Objects.equals(createdDatetime, that.createdDatetime)
        && isDefault.equals(that.isDefault);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, createdDatetime, isDefault);
  }

  @Override
  public String toString() {
    return "ShoppingList{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", createdDatetime="
        + createdDatetime
        + ", isDefault="
        + isDefault
        + ", createdBy="
        + createdBy
        + ", items="
        + items
        + '}';
  }
}
