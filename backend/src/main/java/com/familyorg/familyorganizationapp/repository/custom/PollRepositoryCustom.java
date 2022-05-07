package com.familyorg.familyorganizationapp.repository.custom;

import java.sql.Timestamp;
import java.util.List;
import com.familyorg.familyorganizationapp.domain.Poll;

public interface PollRepositoryCustom {

  List<Poll> getFilteredPolls(List<Long> familyIdFilters, List<Long> pollIdFilters, boolean closed,
      boolean unvoted, Timestamp startDate, Timestamp endDate, long userId);

}
