package com.leafup.leafupbackend.friend.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record FriendshipsResDto(
        List<FriendshipResDto> friendshipResDtos
) {
    public static FriendshipsResDto from(List<FriendshipResDto> friendshipResDtos) {
        return FriendshipsResDto.builder()
                .friendshipResDtos(friendshipResDtos)
                .build();
    }
}
