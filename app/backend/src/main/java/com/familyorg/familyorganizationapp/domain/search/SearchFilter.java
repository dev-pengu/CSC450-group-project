package com.familyorg.familyorganizationapp.domain.search;

import java.io.Serializable;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class SearchFilter implements Serializable {

  @JsonIgnore
  private static final long serialVersionUID = 6373765623518787836L;

  protected Long id;
  protected String display;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDisplay() {
    return display;
  }

  public void setDisplay(String display) {
    this.display = display;
  }

  @Override
  public int hashCode() {
    return Objects.hash(display, id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SearchFilter other = (SearchFilter) obj;
    return Objects.equals(display, other.display)
        && Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return "SearchFilter [id=" + id + ", display=" + display + "]";
  }


}
