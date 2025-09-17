package com.leafup.leafupbackend.store.api.dto.response;

import lombok.Builder;

@Builder
public record StoreAvatarResDto(
        Long avatarId,
        String name,
        String type,
        String avatarUrl,
        int point,
        boolean isOwned,
        boolean isEquipped
) {
    public static StoreAvatarResDto of(Long avatarId, String name, String type, String avatarUrl,
                                       int point, boolean isOwned, boolean isEquipped) {
        return StoreAvatarResDto.builder()
                .avatarId(avatarId)
                .name(name)
                .type(type)
                .avatarUrl(avatarUrl)
                .point(point)
                .isOwned(isOwned)
                .isEquipped(isEquipped)
                .build();
    }
}
