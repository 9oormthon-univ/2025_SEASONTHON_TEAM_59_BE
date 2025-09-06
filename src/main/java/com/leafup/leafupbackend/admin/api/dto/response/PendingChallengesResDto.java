package com.leafup.leafupbackend.admin.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PendingChallengesResDto(
        List<PendingChallengeResDto> challenges
) {
    public static PendingChallengesResDto of(List<PendingChallengeResDto> challenges) {
        return PendingChallengesResDto.builder()
                .challenges(challenges)
                .build();
    }
}
