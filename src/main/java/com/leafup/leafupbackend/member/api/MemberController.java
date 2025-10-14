package com.leafup.leafupbackend.member.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.docs.MemberControllerDocs;
import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.request.UpdateIntroductionReqDto;
import com.leafup.leafupbackend.member.api.dto.request.UpdateNicknameReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.application.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/nickname")
    public ResponseEntity<RspTemplate<Void>> updateNickname(@AuthenticatedEmail String email,
                                                            @Valid @RequestBody UpdateNicknameReqDto updateNicknameReqDto) {
        memberService.updateNickname(email, updateNicknameReqDto);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("닉네임 변경 성공")
                .build()
                .toResponseEntity();
    }

    @PostMapping("/daily-bonus")
    public ResponseEntity<RspTemplate<Void>> claimDailyBonus(@AuthenticatedEmail String email) {
        memberService.claimDailyCompletionBonus(email);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("일일 챌린지 3개 완료 보너스 요청 성공")
                .build()
                .toResponseEntity();
    }

    @PostMapping("/introduction")
    public ResponseEntity<RspTemplate<Void>> updateIntroduction(@AuthenticatedEmail String email,
                                                                @Valid @RequestBody UpdateIntroductionReqDto updateIntroductionReqDto) {
        memberService.updateIntroduction(email, updateIntroductionReqDto);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("소개 변경 성공")
                .build()
                .toResponseEntity();
    }

}
