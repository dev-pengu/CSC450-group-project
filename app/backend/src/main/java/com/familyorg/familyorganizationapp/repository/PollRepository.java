package com.familyorg.familyorganizationapp.repository;

import com.familyorg.familyorganizationapp.domain.Family;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.repository.custom.PollRepositoryCustom;

public interface PollRepository extends CrudRepository<Poll, Long>, PollRepositoryCustom {
  List<Poll> findByFamily(Family family);
}
