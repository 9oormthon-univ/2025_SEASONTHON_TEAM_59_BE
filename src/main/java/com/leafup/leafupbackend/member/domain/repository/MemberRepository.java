package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByNicknameAndCode(String nickname, String code);

    List<Member> findTop100ByOrderByPointDesc();

    List<Member> findTop100ByOrderByStreakDesc();

    long countByPointGreaterThan(int point);

    long countByStreakGreaterThan(int streak);

}
