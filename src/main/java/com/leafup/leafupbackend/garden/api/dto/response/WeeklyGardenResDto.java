package com.leafup.leafupbackend.garden.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;

@Builder
public record WeeklyGardenResDto(
        @Schema(description = "년도")
        int year,
        @Schema(description = "주차")
        int weekOfYear,
        @Schema(description = "완료한 챌린지 목록 (새싹 목록)")
        List<CompletedChallengeDto> completedChallenges
) {
    public static WeeklyGardenResDto of(int year, int weekOfYear, List<CompletedChallengeDto> completedChallenges) {
        return WeeklyGardenResDto.builder()
                .year(year)
                .weekOfYear(weekOfYear)
                .completedChallenges(completedChallenges)
                .build();
    }
}
