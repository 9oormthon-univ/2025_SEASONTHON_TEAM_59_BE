package com.leafup.leafupbackend.member.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateIntroductionReqDto(

        @NotBlank(message = "소개는 공란일 수 없습니다.")
        String introduction

) {
}
