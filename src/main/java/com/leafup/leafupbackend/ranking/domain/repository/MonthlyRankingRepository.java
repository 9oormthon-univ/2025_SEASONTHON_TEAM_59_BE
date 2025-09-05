package com.leafup.leafupbackend.ranking.domain.repository;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.ranking.domain.MonthlyRanking;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyRankingRepository extends JpaRepository<MonthlyRanking, Long> {

    List<MonthlyRanking> findByYearAndMonthAndRegionOrderByPointDesc(int year, int month, String region);

    long countByYearAndMonthAndRegionAndPointGreaterThan(int year, int month, String region, int point);

    Optional<MonthlyRanking> findByYearAndMonthAndRegionAndMember(int year, int month, String region, Member member);

}
