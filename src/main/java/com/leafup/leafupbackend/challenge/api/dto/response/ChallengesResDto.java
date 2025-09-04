package com.leafup.leafupbackend.challenge.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record ChallengesResDto(
        List<ChallengeInfoResDto> challengeInfoResDtos
) {
    public static ChallengesResDto from(List<ChallengeInfoResDto> challengeInfoResDtos) {
        return ChallengesResDto.builder()
                .challengeInfoResDtos(challengeInfoResDtos)
                .build();
    }
}
