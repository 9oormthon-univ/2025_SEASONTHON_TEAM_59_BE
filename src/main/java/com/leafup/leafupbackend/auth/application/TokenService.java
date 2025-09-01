package com.leafup.leafupbackend.auth.application;

import com.leafup.leafupbackend.auth.api.dto.request.RefreshTokenReqDto;
import com.leafup.leafupbackend.auth.exception.InvalidTokenException;
import com.leafup.leafupbackend.global.jwt.TokenProvider;
import com.leafup.leafupbackend.global.jwt.api.dto.TokenDto;
import com.leafup.leafupbackend.global.jwt.domain.Token;
import com.leafup.leafupbackend.global.jwt.domain.repository.TokenRepository;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TokenService {

    private final TokenProvider tokenProvider;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenDto getToken(MemberInfoResDto memberInfoResDto) {
        TokenDto tokenDto = tokenProvider.generateToken(memberInfoResDto.email());

        tokenSaveAndUpdate(memberInfoResDto, tokenDto);

        return tokenDto;
    }

    @Transactional
    public TokenDto generateAccessToken(RefreshTokenReqDto refreshTokenReqDto) {
        if (!tokenRepository.existsByRefreshToken(refreshTokenReqDto.refreshToken())
                || !tokenProvider.isValidToken(refreshTokenReqDto.refreshToken())) {
            throw new InvalidTokenException();
        }

        Token token = tokenRepository.findByRefreshToken(refreshTokenReqDto.refreshToken()).orElseThrow();
        Member member = memberRepository.findById(token.getMember().getId()).orElseThrow(MemberNotFoundException::new);

        return tokenProvider.generateAccessTokenByRefreshToken(member.getEmail(), token.getRefreshToken());
    }

    private void tokenSaveAndUpdate(MemberInfoResDto memberInfoResDto, TokenDto tokenDto) {
        Member member = memberRepository.findByEmail(memberInfoResDto.email()).orElseThrow(MemberNotFoundException::new);

        if (!tokenRepository.existsByMember(member)) {
            tokenRepository.save(Token.builder()
                    .member(member)
                    .refreshToken(tokenDto.refreshToken())
                    .build());
        }

        refreshTokenUpdate(memberInfoResDto, tokenDto);
    }

    private void refreshTokenUpdate(MemberInfoResDto memberInfoResDto, TokenDto tokenDto) {
        Member member = memberRepository.findByEmail(memberInfoResDto.email()).orElseThrow(MemberNotFoundException::new);

        Token token = tokenRepository.findByMember(member).orElseThrow();
        token.refreshTokenUpdate(tokenDto.refreshToken());
    }

}
