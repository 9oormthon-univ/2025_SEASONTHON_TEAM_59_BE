package com.leafup.leafupbackend.ranking.api.dto.response;

import lombok.Builder;

@Builder
public record RankingResDto(
        int rank,
        String nickname,
        String profileImageUrl,
        long score
) {
    public static RankingResDto of(int rank, String nickname, String code, String profileImageUrl, long score) {
        return RankingResDto.builder()
                .rank(rank)
                .nickname(nickname + "#" + code)
                .profileImageUrl(profileImageUrl)
                .score(score)
                .build();
    }
}
