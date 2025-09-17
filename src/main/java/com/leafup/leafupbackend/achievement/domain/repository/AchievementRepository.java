package com.leafup.leafupbackend.achievement.domain.repository;

import com.leafup.leafupbackend.achievement.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}