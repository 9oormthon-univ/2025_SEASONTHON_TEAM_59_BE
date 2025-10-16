package com.leafup.leafupbackend.friend.api.dto.response;

import com.leafup.leafupbackend.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record FriendshipResDto(
        Long friendshipId,
        String nickname,
        String introduction,
        String picture,
        LocalDateTime lastAccessedAt,
        int level,
        double carbonReduction,
        String avatarUrl,
        List<String> achievements
) {
    public static FriendshipResDto of(Member friend, String avatarUrl, List<String> achievements) {
        return FriendshipResDto.builder()
                .friendshipId(friend.getId())
                .nickname(friend.getNickname() + "#" + friend.getCode())
                .introduction(friend.getIntroduction())
                .picture(friend.getPicture())
                .lastAccessedAt(friend.getUpdatedAt())
                .level(friend.getLevel())
                .carbonReduction(friend.getCarbonReduction())
                .avatarUrl(avatarUrl)
                .achievements(achievements)
                .build();
    }
}
