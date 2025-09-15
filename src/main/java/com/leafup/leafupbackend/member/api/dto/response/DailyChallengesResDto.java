package com.leafup.leafupbackend.member.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record DailyChallengesResDto(
        int currentStage,
        int completedCount,
        boolean isRewarded,
        List<String> stageStatus,
        List<DailyChallengeResDto> dailyChallengesResDtos
) {
    public static DailyChallengesResDto of(int currentStage, int completedCount, boolean isRewarded,
                                           List<String> stageStatus,
                                           List<DailyChallengeResDto> dailyChallengeResDtos) {
        return DailyChallengesResDto.builder()
                .currentStage(currentStage)
                .completedCount(completedCount)
                .isRewarded(isRewarded)
                .stageStatus(stageStatus)
                .dailyChallengesResDtos(dailyChallengeResDtos)
                .build();
    }
}
