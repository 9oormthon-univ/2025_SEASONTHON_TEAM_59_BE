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
import java.util.Optional;
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
        Member member = memberRepository.findByEmail(memberInfoResDto.email())
                .orElseThrow(MemberNotFoundException::new);

        TokenDto tokenDto = tokenProvider.generateToken(member.getEmail(), member.getRole());

        tokenSaveOrUpdate(member, tokenDto);

        return tokenDto;
    }

    @Transactional
    public TokenDto generateAccessToken(RefreshTokenReqDto refreshTokenReqDto) {
        if (!tokenProvider.isValidToken(refreshTokenReqDto.refreshToken())) {
            throw new InvalidTokenException();
        }

        Token token = tokenRepository.findByRefreshToken(refreshTokenReqDto.refreshToken())
                .orElseThrow(InvalidTokenException::new);

        Member member = memberRepository.findById(token.getMember().getId())
                .orElseThrow(MemberNotFoundException::new);

        return tokenProvider.generateAccessTokenByRefreshToken(member.getEmail(), member.getRole(), token.getRefreshToken());
    }

    private void tokenSaveOrUpdate(Member member, TokenDto tokenDto) {
        Optional<Token> tokenOpt = tokenRepository.findByMember(member);

        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.refreshTokenUpdate(tokenDto.refreshToken());
        } else {
            tokenRepository.save(Token.builder()
                    .member(member)
                    .refreshToken(tokenDto.refreshToken())
                    .build());
        }
    }

}
