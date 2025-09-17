package com.leafup.leafupbackend.achievement.domain.repository;

import com.leafup.leafupbackend.achievement.domain.MemberAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAchievementRepository extends JpaRepository<MemberAchievement, Long> {
}