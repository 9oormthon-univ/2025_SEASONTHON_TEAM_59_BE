package com.leafup.leafupbackend.garden.api.dto.response;

import lombok.Builder;

@Builder
public record GardenFruitDto(
        Long challengeId,
        String contents,
        boolean isHarvested
) {
    public static GardenFruitDto from(Long challengeId, String contents, boolean isHarvested) {
        return GardenFruitDto.builder()
                .challengeId(challengeId)
                .contents(contents)
                .isHarvested(isHarvested)
                .build();
    }
}