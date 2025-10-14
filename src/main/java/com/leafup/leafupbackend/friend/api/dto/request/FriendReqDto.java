package com.leafup.leafupbackend.friend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FriendReqDto(

        @NotBlank(message = "닉네임과 코드를 입력해주세요.")
        @Pattern(regexp = "^.*#.*$", message = "닉네임#코드 형식으로 입력해주세요.")
        String nicknameWithCode
        
) {
}
