package com.familyorg.familyorganizationapp.repository.custom;

import java.util.List;
import com.familyorg.familyorganizationapp.domain.Poll;

public interface PollRepositoryCustom {

  List<Poll> pollsByFamilyId(Long id, boolean closed, boolean active);

}
