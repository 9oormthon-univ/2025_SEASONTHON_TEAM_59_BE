package com.leafup.leafupbackend.store.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record StoreAvatarsResDto(
        List<StoreAvatarResDto> avatarResDtos
) {
    public static StoreAvatarsResDto from(List<StoreAvatarResDto> avatarResDtos) {
        return StoreAvatarsResDto.builder()
                .avatarResDtos(avatarResDtos)
                .build();
    }
}
