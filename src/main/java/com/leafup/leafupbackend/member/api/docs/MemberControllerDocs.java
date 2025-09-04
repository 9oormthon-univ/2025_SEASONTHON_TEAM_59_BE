package com.leafup.leafupbackend.member.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface MemberControllerDocs {

    @Operation(summary = "온보딩", description = "첫 로그인 후 온보딩합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "온보딩 성공")
    })
    ResponseEntity<RspTemplate<MemberInfoResDto>> onboarding(@AuthenticatedEmail String email,
                                                             @Valid @RequestBody OnboardingReqDto onboardingReqDto);

}
