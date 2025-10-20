package com.leafup.leafupbackend.achievement.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Achievement extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    @Column(nullable = false)
    private int requiredCount;

    @Column(nullable = false)
    private int level;

    @Builder
    private Achievement(String name, String description, AchievementType type, int requiredCount, int level) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredCount = requiredCount;
        this.level = level;
    }

}