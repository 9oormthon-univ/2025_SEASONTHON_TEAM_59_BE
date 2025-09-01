package com.leafup.leafupbackend.auth.api.dto.response;

import com.leafup.leafupbackend.global.jwt.api.dto.TokenDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import lombok.Builder;

@Builder
public record MemberAndTokenResDto(
        TokenDto tokenDto,
        MemberInfoResDto memberInfoResDto
) {
    public static MemberAndTokenResDto of(TokenDto tokenDto, MemberInfoResDto memberInfoResDto) {
        return MemberAndTokenResDto.builder()
                .tokenDto(tokenDto)
                .memberInfoResDto(memberInfoResDto)
                .build();
    }
}
