package com.leafup.leafupbackend.achievement.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AchievementStatusResDto(
        Long id,
        String name,
        String description,
        @Schema(description = "업적 상태 (LOCKED, UNLOCKED, CLAIMED)")
        String status
) {
    public static AchievementStatusResDto of(Long id, String name, String description, String status) {
        return AchievementStatusResDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .status(status)
                .build();
    }
}