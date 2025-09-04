package com.leafup.leafupbackend.challenge.domain.repository;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    @Query("SELECT c FROM Challenge c WHERE c.id NOT IN :excludeIds")
    List<Challenge> findChallengesToSelect(@Param("excludeIds") Set<Long> excludeIds);

    List<Challenge> findByChallengeType(ChallengeType challengeType);

}
