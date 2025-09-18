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
        List<GardenFruitDto> fruits
) {
    public static WeeklyGardenResDto of(int year, int weekOfYear, List<GardenFruitDto> fruits) {
        return WeeklyGardenResDto.builder()
                .year(year)
                .weekOfYear(weekOfYear)
                .fruits(fruits)
                .build();
    }
}
