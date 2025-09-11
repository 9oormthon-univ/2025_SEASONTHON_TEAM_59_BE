package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface DailyMemberChallengeRepository extends JpaRepository<DailyMemberChallenge, Long> {

    List<DailyMemberChallenge> findByMemberAndChallengeDate(Member member, LocalDate date);

    List<DailyMemberChallenge> findByMemberAndChallengeDateAndChallengeStatus(Member member, LocalDate date, ChallengeStatus challengeStatus);

    @Query("SELECT dmc.challenge.id FROM DailyMemberChallenge dmc WHERE dmc.member = :member AND dmc.challengeDate = :date")
    Set<Long> findChallengeIdsByMemberAndChallengeDate(@Param("member") Member member, @Param("date") LocalDate date);

    int countByMemberAndChallengeDateAndChallengeStatus(Member member, LocalDate date, ChallengeStatus challengeStatus);


}
