package com.leafup.leafupbackend.garden.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CompletedChallengeDto(
        @Schema(description = "완료한 챌린지 ID")
        Long challengeId,
        @Schema(description = "완료한 챌린지 내용")
        String content
) {
    public static CompletedChallengeDto of(Long challengeId, String content) {
        return CompletedChallengeDto.builder()
                .challengeId(challengeId)
                .content(content)
                .build();
    }
}
