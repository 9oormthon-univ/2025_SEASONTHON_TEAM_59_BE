package com.leafup.leafupbackend.statistics.api.dto.response;

import lombok.Builder;

@Builder
public record GlobalStatisticsResDto(
        double totalCarbonReduction,
        long serviceOperatingDays,
        double dailyAverageReduction,
        double treesPlantedEffect,
        double carEmissionReductionEffect
) {
    public static GlobalStatisticsResDto of(double totalCarbonReduction, long serviceOperatingDays,
                                            double dailyAverageReduction, double treesPlantedEffect,
                                            double carEmissionReductionEffect) {
        return GlobalStatisticsResDto.builder()
                .totalCarbonReduction(totalCarbonReduction)
                .serviceOperatingDays(serviceOperatingDays)
                .dailyAverageReduction(dailyAverageReduction)
                .treesPlantedEffect(treesPlantedEffect)
                .carEmissionReductionEffect(carEmissionReductionEffect)
                .build();
    }
}