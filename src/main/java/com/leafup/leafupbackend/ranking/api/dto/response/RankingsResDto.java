package com.leafup.leafupbackend.ranking.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record RankingsResDto(
        List<RankingResDto> rankings
) {
    public static RankingsResDto of(List<RankingResDto> rankings) {
        return RankingsResDto.builder()
                .rankings(rankings)
                .build();
    }
}
