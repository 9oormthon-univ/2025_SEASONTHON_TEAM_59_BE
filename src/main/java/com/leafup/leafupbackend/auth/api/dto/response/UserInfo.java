package com.leafup.leafupbackend.auth.api.dto.response;

public record UserInfo(
        String email,
        String name,
        String nickname,
        String picture
) {
}
