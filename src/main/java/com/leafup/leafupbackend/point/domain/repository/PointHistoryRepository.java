package com.leafup.leafupbackend.point.domain.repository;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.point.application.dto.MonthlyPointAggregationDto;
import com.leafup.leafupbackend.point.application.dto.PointAggregationDto;
import com.leafup.leafupbackend.point.domain.PointHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("SELECT new com.leafup.leafupbackend.point.application.dto.MonthlyPointAggregationDto(ph.member, SUM(ph.amount)) " +
            "FROM PointHistory ph " +
            "WHERE YEAR(ph.createdAt) = :year AND MONTH(ph.createdAt) = :month " +
            "GROUP BY ph.member")
    List<MonthlyPointAggregationDto> aggregateMonthlyPoints(@Param("year") int year, @Param("month") int month);

    @Query("SELECT new com.leafup.leafupbackend.point.application.dto.PointAggregationDto(ph.member, SUM(ph.amount)) " +
            "FROM PointHistory ph " +
            "WHERE ph.amount > 0 " +
            "GROUP BY ph.member " +
            "ORDER BY SUM(ph.amount) DESC")
    List<PointAggregationDto> findTotalPointsByMember();

    @Query("SELECT SUM(ph.amount) FROM PointHistory ph WHERE ph.member = :member AND ph.amount > 0")
    Optional<Long> findTotalPointsBySingleMember(@Param("member") Member member);

    @Query("SELECT ph.member FROM PointHistory ph WHERE ph.amount > 0 GROUP BY ph.member HAVING SUM(ph.amount) > :score")
    List<Member> findMembersWithHigherTotalPoints(@Param("score") long score);

}
