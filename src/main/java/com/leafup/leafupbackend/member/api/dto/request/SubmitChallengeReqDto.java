package com.leafup.leafupbackend.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SubmitChallengeReqDto(
        @NotBlank(message = "이미지가 없으면 인증이 불가능합니다.")
        String imageUrl
) {
}
