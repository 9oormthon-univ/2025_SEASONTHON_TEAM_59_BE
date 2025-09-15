package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMemberChallengeRepository extends JpaRepository<DailyMemberChallenge, Long> {

    List<DailyMemberChallenge> findByMemberAndChallengeDate(Member member, LocalDate date);

    List<DailyMemberChallenge> findTop5ByMemberAndChallengeDateAndChallengeStatusOrderByIdDesc(Member member, LocalDate date, ChallengeStatus challengeStatus);

    List<DailyMemberChallenge> findTop5ByMemberAndChallengeDateOrderByIdDesc(Member member, LocalDate date);

    int countByMemberAndChallengeDateAndChallengeStatus(Member member, LocalDate date, ChallengeStatus challengeStatus);

    long countByMemberAndChallengeStatus(Member member, ChallengeStatus challengeStatus);

    @Query("SELECT COALESCE(SUM(c.carbonReduction), 0.0) " +
            "FROM DailyMemberChallenge dmc JOIN dmc.challenge c " +
            "WHERE dmc.member = :member AND dmc.challengeStatus = :status")
    double sumTotalCarbonReductionByMember(@Param("member") Member member, @Param("status") ChallengeStatus status);

    @Query("SELECT COALESCE(SUM(c.carbonReduction), 0.0) " +
            "FROM DailyMemberChallenge dmc JOIN dmc.challenge c " +
            "WHERE dmc.challengeStatus = :status")
    double sumTotalCarbonReductionForAllUsers(@Param("status") ChallengeStatus status);
    
    @Query("SELECT MIN(dmc.challengeDate) FROM DailyMemberChallenge dmc")
    Optional<LocalDate> findMinChallengeDate();

}
