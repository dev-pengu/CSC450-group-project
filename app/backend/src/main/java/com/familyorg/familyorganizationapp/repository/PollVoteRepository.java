package com.familyorg.familyorganizationapp.repository;

import org.springframework.data.repository.CrudRepository;
import com.familyorg.familyorganizationapp.domain.PollVote;
import com.familyorg.familyorganizationapp.domain.id.VoteId;
import com.familyorg.familyorganizationapp.repository.custom.PollVoteRepositoryCustom;

public interface PollVoteRepository
    extends CrudRepository<PollVote, VoteId>, PollVoteRepositoryCustom {

}
