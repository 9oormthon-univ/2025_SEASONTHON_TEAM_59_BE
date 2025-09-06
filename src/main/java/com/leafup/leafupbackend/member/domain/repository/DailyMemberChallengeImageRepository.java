package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.DailyMemberChallengeImage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyMemberChallengeImageRepository extends JpaRepository<DailyMemberChallengeImage, Long> {

    List<DailyMemberChallengeImage> findByDailyMemberChallengeIn(Collection<DailyMemberChallenge> dailyMemberChallenges);

    @Query("SELECT dci FROM DailyMemberChallengeImage dci " +
            "JOIN FETCH dci.dailyMemberChallenge dmc " +
            "JOIN FETCH dmc.member " +
            "JOIN FETCH dmc.challenge " +
            "WHERE dmc.challengeStatus = :status")
    List<DailyMemberChallengeImage> findPendingChallengesWithDetails(@Param("status") ChallengeStatus status);

}
