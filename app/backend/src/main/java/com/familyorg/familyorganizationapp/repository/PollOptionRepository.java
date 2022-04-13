package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.PollOption;
import com.familyorg.familyorganizationapp.repository.custom.PollOptionRepositoryCustom;

public interface PollOptionRepository
    extends CrudRepository<PollOption, Long>, PollOptionRepositoryCustom {

}
