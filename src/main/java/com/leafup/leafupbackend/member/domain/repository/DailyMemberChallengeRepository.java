package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyMemberChallengeRepository extends JpaRepository<DailyMemberChallenge, Long> {

    List<DailyMemberChallenge> findByMemberAndChallengeDate(Member member, LocalDate date);

    List<DailyMemberChallenge> findTop5ByMemberAndChallengeDateAndChallengeStatusOrderByIdDesc(Member member, LocalDate date, ChallengeStatus challengeStatus);

    List<DailyMemberChallenge> findTop5ByMemberAndChallengeDateOrderByIdDesc(Member member, LocalDate date);

    int countByMemberAndChallengeDateAndChallengeStatus(Member member, LocalDate date, ChallengeStatus challengeStatus);

}
