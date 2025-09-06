package com.leafup.leafupbackend.garden.domain.repository;

import com.leafup.leafupbackend.garden.domain.WeeklyGarden;
import com.leafup.leafupbackend.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyGardenRepository extends JpaRepository<WeeklyGarden, Long> {

    /**
     * 특정 사용자의 특정 년도, 특정 주차의 텃밭 정보를 조회합니다.
     */
    Optional<WeeklyGarden> findByMemberAndYearAndWeekOfYear(Member member, int year, int weekOfYear);

}
