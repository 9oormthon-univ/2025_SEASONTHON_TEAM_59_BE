package com.leafup.leafupbackend.admin.api.dto.response;

import lombok.Builder;

@Builder
public record PendingChallengeResDto(
        Long dailyMemberChallengeId,
        String userNickname,
        String challengeContent,
        String imageUrl
) {
    public static PendingChallengeResDto of(Long dailyMemberChallengeId, String userNickname, String code,
                                            String challengeContent, String imageUrl) {
        return PendingChallengeResDto.builder()
                .dailyMemberChallengeId(dailyMemberChallengeId)
                .userNickname(userNickname + "#" + code)
                .challengeContent(challengeContent)
                .imageUrl(imageUrl)
                .build();
    }
}
