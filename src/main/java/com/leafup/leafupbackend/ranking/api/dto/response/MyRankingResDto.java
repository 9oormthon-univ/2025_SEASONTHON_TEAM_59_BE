package com.leafup.leafupbackend.ranking.api.dto.response;

import lombok.Builder;

@Builder
public record MyRankingResDto(
        long rank,
        String nickname,
        String profileImageUrl,
        long score
) {
    public static MyRankingResDto of(long rank, String nickname, String profileImageUrl, long score) {
        return MyRankingResDto.builder()
                .rank(rank)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .score(score)
                .build();
    }
}
