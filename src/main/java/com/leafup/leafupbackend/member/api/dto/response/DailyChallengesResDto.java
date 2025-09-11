package com.leafup.leafupbackend.member.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record DailyChallengesResDto(
        int currentStage,
        int completedCount,
        List<String> stageStatus,
        List<DailyChallengeResDto> dailyChallengesResDtos
) {
    public static DailyChallengesResDto of(int currentStage, int completedCount, List<String> stageStatus,
                                           List<DailyChallengeResDto> dailyChallengeResDtos) {
        return DailyChallengesResDto.builder()
                .currentStage(currentStage)
                .completedCount(completedCount)
                .stageStatus(stageStatus)
                .dailyChallengesResDtos(dailyChallengeResDtos)
                .build();
    }
}
