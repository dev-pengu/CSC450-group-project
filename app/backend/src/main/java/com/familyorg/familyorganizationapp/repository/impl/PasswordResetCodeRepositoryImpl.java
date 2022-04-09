package com.familyorg.familyorganizationapp.repository.impl;

import com.familyorg.familyorganizationapp.domain.PasswordResetCode;
import com.familyorg.familyorganizationapp.domain.QPasswordResetCode;
import com.familyorg.familyorganizationapp.repository.custom.PasswordResetCodeRepositoryCustom;
import com.querydsl.jpa.impl.JPAUpdateClause;
import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordResetCodeRepositoryImpl extends QuerydslRepositorySupport
    implements PasswordResetCodeRepositoryCustom {

  QPasswordResetCode codeTable = QPasswordResetCode.passwordResetCode;

  public PasswordResetCodeRepositoryImpl() {
    super(PasswordResetCode.class);
  }

  @Override
  public List<PasswordResetCode> getResetCodesByUser(Long userId) {
    List<PasswordResetCode> codes =
        from(codeTable)
            .where(codeTable.user.id.eq(userId).and(codeTable.expired.eq(false)))
            .fetch();
    return codes;
  }

  @Override
  public void batchExpire(List<Long> codeIds) {
    new JPAUpdateClause(getEntityManager(), codeTable)
        .where(codeTable.id.in(codeIds))
        .set(codeTable.expired, true)
        .execute();
  }
}
