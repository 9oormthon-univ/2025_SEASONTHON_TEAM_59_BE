package com.leafup.leafupbackend.garden.domain.repository;

import com.leafup.leafupbackend.garden.domain.WeeklyGarden;
import com.leafup.leafupbackend.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyGardenRepository extends JpaRepository<WeeklyGarden, Long> {

    Optional<WeeklyGarden> findByMemberAndYearAndWeekOfYear(Member member, int year, int weekOfYear);

}
