package com.leafup.leafupbackend.statistics.api.dto.response;

import lombok.Builder;

@Builder
public record MyStatisticsResDto(
        double totalCarbonReduction,
        double treesPlantedEffect,
        double carEmissionReductionEffect
) {
    public static MyStatisticsResDto of(double totalCarbonReduction, double treesPlantedEffect, double carEmissionReduction) {
        return MyStatisticsResDto.builder()
                .totalCarbonReduction(totalCarbonReduction)
                .treesPlantedEffect(treesPlantedEffect)
                .carEmissionReductionEffect(carEmissionReduction)
                .build();
    }
}