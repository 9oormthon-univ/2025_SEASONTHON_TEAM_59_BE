package com.leafup.leafupbackend.achievement.domain;

import lombok.Getter;

@Getter
public enum AchievementType {
    FIRST_CHALLENGE("첫 챌린지 완료"),
    STAGE_PARTICIPATION("스테이지 참여"),
    DAILY_HARVEST("일일 완주"),
    GARDENER("텃밭 가꾸기"),
    LEAF_COLLECTOR("상점 이용");

    private final String description;

    AchievementType(String description) {
        this.description = description;
    }

}