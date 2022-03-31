package com.familyorg.familyorganizationapp.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.domain.QUser;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.repository.custom.UserRepositoryCustom;
import com.querydsl.jpa.impl.JPAUpdateClause;

@Repository
public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

  private QUser userTable = QUser.user;

  @Autowired
  EntityManager entityManager;

  public UserRepositoryImpl() {
    super(User.class);
  }

  @Override
  public void updateLastLoggedIn(String username) {
    new JPAUpdateClause(entityManager, userTable)
        .where(userTable.username.equalsIgnoreCase(username))
        .set(userTable.lastLoggedIn, Timestamp.from(Instant.now()))
        .execute();
  }
}
