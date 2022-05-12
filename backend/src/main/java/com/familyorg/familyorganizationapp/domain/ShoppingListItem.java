package com.familyorg.familyorganizationapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Entity
@Table(name = "shopping_list_item")
public class ShoppingListItem implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shopping_list_item_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @Column(name = "description", columnDefinition = "VARCHAR(50)", nullable = false)
  private String description;

  @Column(name = "amount", columnDefinition = "NUMERIC")
  private Double amount;

  @Column(name = "unit", columnDefinition = "VARCHAR(10)")
  private String unit;

  @Column(name = "notes", columnDefinition = "TEXT")
  private String notes;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "shopping_list_id",
      referencedColumnName = "shopping_list_id",
      columnDefinition = "BIGINT")
  private ShoppingList list;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "added_by", referencedColumnName = "user_id", columnDefinition = "BIGINT")
  private User addedBy;

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

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit == null ? null : unit.trim();
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes == null ? null : notes.trim();
  }

  public ShoppingList getList() {
    return list;
  }

  public void setList(ShoppingList list) {
    this.list = list;
  }

  public User getAddedBy() {
    return addedBy;
  }

  public void setAddedBy(User addedBy) {
    this.addedBy = addedBy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ShoppingListItem that = (ShoppingListItem) o;
    return id.equals(that.id)
        && description.equals(that.description)
        && Objects.equals(amount, that.amount)
        && Objects.equals(unit, that.unit)
        && Objects.equals(notes, that.notes)
        && addedBy.equals(that.addedBy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, amount, unit, notes, addedBy);
  }

  @Override
  public String toString() {
    return "ShoppingListItem{"
        + "id="
        + id
        + ", description='"
        + description
        + '\''
        + ", amount="
        + amount
        + ", measurement='"
        + unit
        + '\''
        + ", notes='"
        + notes
        + '\''
        + ", addedBy="
        + addedBy
        + '}';
  }
}
