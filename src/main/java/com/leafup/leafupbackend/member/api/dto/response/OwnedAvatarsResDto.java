package com.leafup.leafupbackend.member.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record OwnedAvatarsResDto(
        List<OwnedAvatarResDto> ownedAvatarResDtos
) {
    public static OwnedAvatarsResDto from(List<OwnedAvatarResDto> ownedAvatarResDtos) {
        return OwnedAvatarsResDto.builder()
                .ownedAvatarResDtos(ownedAvatarResDtos)
                .build();
    }
}
