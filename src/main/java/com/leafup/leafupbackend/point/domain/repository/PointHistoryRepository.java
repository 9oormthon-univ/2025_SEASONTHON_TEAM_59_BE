package com.leafup.leafupbackend.point.domain.repository;

import com.leafup.leafupbackend.point.application.dto.MonthlyPointAggregationDto;
import com.leafup.leafupbackend.point.domain.PointHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("SELECT new com.leafup.leafupbackend.point.application.dto.MonthlyPointAggregationDto(ph.member, SUM(ph.amount)) " +
           "FROM PointHistory ph " +
           "WHERE YEAR(ph.createdAt) = :year AND MONTH(ph.createdAt) = :month " +
           "GROUP BY ph.member")
    List<MonthlyPointAggregationDto> aggregateMonthlyPoints(@Param("year") int year, @Param("month") int month);

}
