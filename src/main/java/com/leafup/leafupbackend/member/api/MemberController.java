package com.leafup.leafupbackend.member.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.docs.MemberControllerDocs;
import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.application.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<RspTemplate<MemberInfoResDto>> getInfo(@AuthenticatedEmail String email) {
        return RspTemplate.<MemberInfoResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("사용자 정보 조회")
                .data(memberService.getInfo(email))
                .build()
                .toResponseEntity();
    }

    @PatchMapping("/onboarding")
    public ResponseEntity<RspTemplate<MemberInfoResDto>> onboarding(@AuthenticatedEmail String email,
                                                                    @Valid @RequestBody OnboardingReqDto onboardingReqDto) {
        return RspTemplate.<MemberInfoResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("온보딩 사용자 정보 저장 성공")
                .data(memberService.onboarding(email, onboardingReqDto))
                .build()
                .toResponseEntity();
    }

}
