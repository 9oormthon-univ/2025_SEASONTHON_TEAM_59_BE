package com.leafup.leafupbackend.achievement.api.dto.response;

import lombok.Builder;

@Builder
public record ClaimedAchievementResDto(
        Long id,
        String name,
        String description
) {
    public static ClaimedAchievementResDto of(Long id, String name, String description) {
        return ClaimedAchievementResDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();
    }
}