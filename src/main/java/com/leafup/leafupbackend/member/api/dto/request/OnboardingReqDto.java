package com.leafup.leafupbackend.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record OnboardingReqDto(

        @NotBlank(message = "닉네임은 공란일 수 없습니다.")
        String nickname,

        boolean locationAgreed,

        boolean cameraAccessAllowed,

        @NotBlank(message = "주소는 공란일 수 없습니다.")
        String address
        
) {
}
