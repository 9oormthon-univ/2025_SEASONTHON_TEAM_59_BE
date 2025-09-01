package com.leafup.leafupbackend.auth.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.auth.api.dto.request.RefreshTokenReqDto;
import com.leafup.leafupbackend.auth.api.dto.response.MemberAndTokenResDto;
import com.leafup.leafupbackend.auth.api.dto.response.UserInfo;
import com.leafup.leafupbackend.auth.application.AuthMemberService;
import com.leafup.leafupbackend.auth.application.AuthService;
import com.leafup.leafupbackend.auth.application.AuthServiceFactory;
import com.leafup.leafupbackend.auth.application.TokenService;
import com.leafup.leafupbackend.global.jwt.api.dto.TokenDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.domain.SocialType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final AuthServiceFactory authServiceFactory;
    private final AuthMemberService memberService;
    private final TokenService tokenService;

    @GetMapping("/login")
    public void redirectToProvider(@RequestParam("provider") String provider, HttpServletResponse response) throws IOException {
        AuthService authService = authServiceFactory.getAuthService(provider);
        if (authService.getAuthUrl() == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "지원하지 않는 소셜 로그인입니다.");
            return;
        }

        response.sendRedirect(authService.getAuthUrl());
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<RspTemplate<MemberAndTokenResDto>> generateAccessAndRefreshToken(
            @Parameter(name = "provider", description = "소셜 타입(google, kakao)", in = ParameterIn.PATH)
            @PathVariable(name = "provider") String provider,
            @RequestParam("code") String code) {
        AuthService authService = authServiceFactory.getAuthService(provider);
        UserInfo userInfo = authService.getUserInfo(code);

        MemberInfoResDto getMemberDto = memberService.saveUserInfo(userInfo,
                SocialType.valueOf(provider.toUpperCase()));
        TokenDto getToken = tokenService.getToken(getMemberDto);

        return RspTemplate.<MemberAndTokenResDto>builder()
                .statusCode(HttpStatus.OK)
                .data(MemberAndTokenResDto.of(getToken, getMemberDto))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/token/access")
    public ResponseEntity<RspTemplate<TokenDto>> generateAccessToken(@RequestBody RefreshTokenReqDto refreshTokenReqDto) {
        TokenDto getToken = tokenService.generateAccessToken(refreshTokenReqDto);

        return RspTemplate.<TokenDto>builder()
                .statusCode(HttpStatus.OK)
                .message("엑세스 토큰 발급")
                .data(getToken)
                .build()
                .toResponseEntity();
    }

}
