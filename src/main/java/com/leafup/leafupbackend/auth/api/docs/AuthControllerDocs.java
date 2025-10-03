package com.leafup.leafupbackend.auth.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.auth.api.dto.response.MemberAndTokenResDto;
import com.leafup.leafupbackend.auth.util.CookieUtil;
import com.leafup.leafupbackend.global.jwt.api.dto.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface AuthControllerDocs {

    @Operation(summary = "1. 로그인 주소 발급", description = "발급 받은 주소를 \"window.location.href = {통신 후 응답 받은 값}\" 에 넣으면 로그인 화면으로 전환됩니다. 로그인 후 redirect uri로 핸들링 하면 됩니다. 핸들링 할 때 2번 자체 AccessToken, RefreshToken 발급 API 사용하셔서 토큰 발급 하시면 됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공")
    })
    void redirectToProvider(@RequestParam("provider") String provider, HttpServletResponse response) throws IOException;

    @Operation(summary = "2. 자체 AccessToken, RefreshToken 발급", description = "code를 이용하여 액세스, 리프레쉬 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공")
    })
    ResponseEntity<RspTemplate<MemberAndTokenResDto>> generateAccessAndRefreshToken(
            @Parameter(name = "provider", description = "소셜 타입(google, kakao)", in = ParameterIn.PATH)
            @PathVariable(name = "provider") String provider,
            @RequestParam("code") String code,
            HttpServletResponse response);

    @Operation(summary = "액세스 토큰 재발급", description = "리프레쉬 토큰으로 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공")
    })
    ResponseEntity<RspTemplate<TokenDto>> generateAccessToken(
            @CookieValue(name = CookieUtil.REFRESH_TOKEN_COOKIE_NAME) String refreshToken);

}
