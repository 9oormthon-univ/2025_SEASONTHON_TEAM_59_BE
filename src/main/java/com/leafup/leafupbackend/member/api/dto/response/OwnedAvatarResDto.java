package com.leafup.leafupbackend.member.api.dto.response;

import lombok.Builder;

@Builder
public record OwnedAvatarResDto(
        Long avatarId,
        String avatarUrl,
        boolean isEquipped
) {
    public static OwnedAvatarResDto from(Long avatarId, String avatarUrl, boolean isEquipped) {
        return OwnedAvatarResDto.builder()
                .avatarId(avatarId)
                .avatarUrl(avatarUrl)
                .isEquipped(isEquipped)
                .build();
    }
}
