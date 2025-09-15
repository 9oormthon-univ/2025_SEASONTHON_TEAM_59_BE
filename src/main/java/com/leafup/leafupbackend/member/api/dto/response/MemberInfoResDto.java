package com.leafup.leafupbackend.member.api.dto.response;

import lombok.Builder;

@Builder
public record MemberInfoResDto(
        String email,
        String picture,
        String socialType,
        boolean isFirstLogin,
        String nickname,
        boolean isLocationAgreed,
        boolean isCameraAccessAllowed,
        int level,
        int exp,
        int point,
        String avatarUrl
) {
    public static MemberInfoResDto of(String email, String picture, String socialType,
                                      boolean isFirstLogin, String nickname, String code,
                                      boolean isLocationAgreed, boolean isCameraAccessAllowed,
                                      int level, int exp, int point,
                                      String avatarUrl) {
        return MemberInfoResDto.builder()
                .email(email)
                .picture(picture)
                .socialType(socialType)
                .isFirstLogin(isFirstLogin)
                .nickname(nickname + "#" + code)
                .isLocationAgreed(isLocationAgreed)
                .isCameraAccessAllowed(isCameraAccessAllowed)
                .level(level)
                .exp(exp)
                .point(point)
                .avatarUrl(avatarUrl)
                .build();
    }
}
