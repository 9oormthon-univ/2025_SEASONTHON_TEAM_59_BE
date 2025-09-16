package com.leafup.leafupbackend.statistics.api.dto.response;

import lombok.Builder;

@Builder
public record GlobalStatisticsResDto(
        double totalCarbonReduction,
        long totalMemberCount,
        double dailyAverageReduction,
        double treesPlantedEffect,
        double carEmissionReductionEffect
) {
    public static GlobalStatisticsResDto of(double totalCarbonReduction, long totalMemberCont,
                                            double dailyAverageReduction, double treesPlantedEffect,
                                            double carEmissionReductionEffect) {
        return GlobalStatisticsResDto.builder()
                .totalCarbonReduction(totalCarbonReduction)
                .totalMemberCount(totalMemberCont)
                .dailyAverageReduction(dailyAverageReduction)
                .treesPlantedEffect(treesPlantedEffect)
                .carEmissionReductionEffect(carEmissionReductionEffect)
                .build();
    }
}