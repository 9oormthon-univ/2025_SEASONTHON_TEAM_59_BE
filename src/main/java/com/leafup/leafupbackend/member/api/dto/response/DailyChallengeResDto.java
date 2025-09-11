package com.leafup.leafupbackend.member.api.dto.response;

import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import lombok.Builder;

@Builder
public record DailyChallengeResDto(
        Long dailyMemberChallengeId,
        String contents,
        ChallengeType challengeType,
        ChallengeStatus challengeStatus,
        boolean isTwoCut
) {
    public static DailyChallengeResDto of(Long dailyMemberChallengeId, String contents, ChallengeType challengeType,
                                          ChallengeStatus challengeStatus, boolean isTwoCut) {
        return DailyChallengeResDto.builder()
                .dailyMemberChallengeId(dailyMemberChallengeId)
                .contents(contents)
                .challengeType(challengeType)
                .challengeStatus(challengeStatus)
                .isTwoCut(isTwoCut)
                .build();
    }
}