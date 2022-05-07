package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import com.familyorg.familyorganizationapp.DTO.ColorDto;
import com.familyorg.familyorganizationapp.DTO.builder.ColorDtoBuilder;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.QFamilyMembers;
import com.familyorg.familyorganizationapp.repository.custom.FamilyMemberRepositoryCustom;
import com.querydsl.core.Tuple;

@Repository
public class FamilyMemberRepositoryImpl extends QuerydslRepositorySupport
    implements FamilyMemberRepositoryCustom {
  public FamilyMemberRepositoryImpl() {
    super(FamilyMembers.class);
  }

  private QFamilyMembers familyMembersTable = QFamilyMembers.familyMembers;

  @Override
  public List<ColorDto> getColorsByUser(Long userId) {
    List<Tuple> tuples =
        from(familyMembersTable)
            .select(familyMembersTable.family.id, familyMembersTable.eventColor,
                familyMembersTable.family.name)
            .where(familyMembersTable.user.id.eq(userId))
            .fetch();

    return tuples.stream()
        .map(tuple -> new ColorDtoBuilder()
            .withFamilyId(tuple.get(0, Long.class))
            .withColor(tuple.get(1, String.class))
            .withFamily(tuple.get(2, String.class))
            .withUserId(userId)
            .build())
        .collect(Collectors.toList());
  }
}
