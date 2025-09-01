package com.leafup.leafupbackend.member.api.dto.request;

public record OnboardingReqDto(
        String nickname,
        boolean locationAgreed,
        boolean cameraAccessAllowed
) {
}
