package com.leafup.leafupbackend.challenge.api.dto.response;

import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import lombok.Builder;

@Builder
public record ChallengeInfoResDto(
        String contents,
        int point,
        ChallengeType challengeType
) {
    public static ChallengeInfoResDto of(String contents, int point, ChallengeType challengeType) {
        return ChallengeInfoResDto.builder()
                .contents(contents)
                .point(point)
                .challengeType(challengeType)
                .build();
    }
}
