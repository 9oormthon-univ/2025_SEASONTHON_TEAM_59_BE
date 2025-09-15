package com.leafup.leafupbackend.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateNicknameReqDto(

        @NotBlank(message = "닉네임은 공란일 수 없습니다.")
        String nickname
        
) {
}
