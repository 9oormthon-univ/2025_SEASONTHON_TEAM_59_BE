package com.leafup.leafupbackend.friend.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record FriendResDto(
        Long memberId,
        String nickname,
        String picture,
        LocalDateTime lastAccessedAt
) {
    public static FriendResDto of(Long memberId, String nickname, String code, String picture, LocalDateTime lastAccessedAt) {
        return FriendResDto.builder()
                .memberId(memberId)
                .nickname(nickname + "#" + code)
                .picture(picture)
                .lastAccessedAt(lastAccessedAt)
                .build();
    }
}
