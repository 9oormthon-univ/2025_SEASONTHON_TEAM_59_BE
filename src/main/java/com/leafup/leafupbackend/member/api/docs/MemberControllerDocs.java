package com.leafup.leafupbackend.member.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.request.UpdateIntroductionReqDto;
import com.leafup.leafupbackend.member.api.dto.request.UpdateNicknameReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(summary = "사용자 정보 조회", description = "사용자 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    })
    ResponseEntity<RspTemplate<MemberInfoResDto>> getInfo(@AuthenticatedEmail String email);

    @Operation(summary = "온보딩", description = "첫 로그인 후 온보딩합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "온보딩 성공")
    })
    ResponseEntity<RspTemplate<MemberInfoResDto>> onboarding(@AuthenticatedEmail String email,
                                                             @Valid @RequestBody OnboardingReqDto onboardingReqDto);

    @Operation(summary = "사용자 닉네임 변경", description = "사용자 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공")
    })
    ResponseEntity<RspTemplate<Void>> updateNickname(@AuthenticatedEmail String email,
                                                     @Valid @RequestBody UpdateNicknameReqDto updateNicknameReqDto);

    @Operation(summary = "일일 챌린지 3개 완료 보너스", description = "일일 챌린지 3개 완료 보너스를 요청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일일 챌린지 3개 완료 보너스 요청 성공")
    })
    ResponseEntity<RspTemplate<Void>> claimDailyBonus(@AuthenticatedEmail String email);

    @Operation(summary = "사용자 소개 변경", description = "사용자 소개를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소개 변경 성공")
    })
    ResponseEntity<RspTemplate<Void>> updateIntroduction(@AuthenticatedEmail String email,
                                                         @Valid @RequestBody UpdateIntroductionReqDto updateIntroductionReqDto);

}
