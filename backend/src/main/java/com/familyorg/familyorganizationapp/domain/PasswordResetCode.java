package com.familyorg.familyorganizationapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.sql.Timestamp;
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
@Table(name = "password_reset_code")
public class PasswordResetCode implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "password_reset_code_id", columnDefinition = "BIGSERIAL")
  private Long id;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "user_id",
      referencedColumnName = "user_id",
      columnDefinition = "BIGINT",
      nullable = false)
  private User user;

  @Column(name = "reset_code", columnDefinition = "VARCHAR(36)", nullable = false)
  private String resetCode;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private Timestamp created;

  @Column(name = "expired", columnDefinition = "BOOLEAN")
  private Boolean expired = false;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getResetCode() {
    return resetCode;
  }

  public void setResetCode(String resetCode) {
    this.resetCode = resetCode;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }

  public Boolean getExpired() {
    return expired;
  }

  public void setExpired(Boolean expired) {
    this.expired = expired;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PasswordResetCode that = (PasswordResetCode) o;
    return id.equals(that.id)
        && user.equals(that.user)
        && resetCode.equals(that.resetCode)
        && created.equals(that.created)
        && Objects.equals(expired, that.expired);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, user, resetCode, created, expired);
  }

  @Override
  public String toString() {
    return "PasswordResetCode{"
        + "id="
        + id
        + ", user="
        + user
        + ", resetCode='"
        + resetCode
        + '\''
        + ", created="
        + created
        + ", expired="
        + expired
        + '}';
  }
}
