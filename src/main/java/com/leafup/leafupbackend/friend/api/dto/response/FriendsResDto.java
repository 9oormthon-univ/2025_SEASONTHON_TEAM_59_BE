package com.leafup.leafupbackend.friend.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record FriendsResDto(
        List<FriendResDto> friendResDtos
) {
    public static FriendsResDto from(List<FriendResDto> friendResDtos) {
        return FriendsResDto.builder()
                .friendResDtos(friendResDtos)
                .build();
    }
}
