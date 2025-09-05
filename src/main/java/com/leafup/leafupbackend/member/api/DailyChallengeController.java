package com.leafup.leafupbackend.member.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.docs.DailyChallengeControllerDocs;
import com.leafup.leafupbackend.member.api.dto.request.SubmitChallengeReqDto;
import com.leafup.leafupbackend.member.api.dto.response.DailyChallengesResDto;
import com.leafup.leafupbackend.member.application.DailyChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/daily-challenges")
public class DailyChallengeController implements DailyChallengeControllerDocs {

    private final DailyChallengeService dailyChallengeService;

    @GetMapping("/today")
    public ResponseEntity<RspTemplate<DailyChallengesResDto>> getTodaysChallenges(@AuthenticatedEmail String email) {
        return RspTemplate.<DailyChallengesResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("오늘의 챌린지 목록 조회")
                .data(dailyChallengeService.getOrCreateTodaysChallenges(email))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/{dailyMemberChallengeId}/submit")
    public ResponseEntity<RspTemplate<Void>> submitChallenge(
            @AuthenticatedEmail String email,
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId,
            @Valid @RequestBody SubmitChallengeReqDto submitChallengeReqDto) {
        dailyChallengeService.submitChallenge(email, dailyMemberChallengeId, submitChallengeReqDto.imageUrl());
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("챌린지 인증 요청 성공")
                .build()
                .toResponseEntity();
    }

}
