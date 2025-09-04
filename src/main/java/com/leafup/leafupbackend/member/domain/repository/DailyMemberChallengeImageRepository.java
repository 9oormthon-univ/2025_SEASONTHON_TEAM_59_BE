package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.DailyMemberChallengeImage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyMemberChallengeImageRepository extends JpaRepository<DailyMemberChallengeImage, Long> {

    List<DailyMemberChallengeImage> findByDailyMemberChallengeIn(Collection<DailyMemberChallenge> dailyMemberChallenges);
    
}
