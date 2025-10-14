package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByNicknameAndCode(String nickname, String code);

    List<Member> findTop100ByOrderByPointDesc();

    List<Member> findTop100ByOrderByStreakDesc();

    long countByPointGreaterThan(int point);

    long countByStreakGreaterThan(int streak);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.memberAchievements WHERE m.email = :email")
    Optional<Member> findByEmailWithAchievements(@Param("email") String email);

    Optional<Member> findByNicknameAndCode(String nickname, String code);

}
