package com.leafup.leafupbackend.member.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.dto.request.SubmitChallengeReqDto;
import com.leafup.leafupbackend.member.api.dto.response.DailyChallengesResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface DailyChallengeControllerDocs {

    @Operation(summary = "오늘의 챌린지, 사용자의 현재 스테이지 조회", description = "오늘의 챌린지 5개와 사용자의 현재 스테이지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오늘의 챌린지 조회 성공")
    })
    ResponseEntity<RspTemplate<DailyChallengesResDto>> getTodaysChallenges(@AuthenticatedEmail String email);

    @Operation(summary = "챌린지 사진 인증하기(사진 제출하기)", description = "챌린지를 인증합니다. ImageController에 있는 챌린지 이미지 업로드 API를 사용하면 반환값으로 imageUrl이 나옵니다. 그 값을 제출하기 버튼 누를 때 요청 값으로 보내면됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 인증 성공")
    })
    ResponseEntity<RspTemplate<Void>> submitChallenge(
            @AuthenticatedEmail String email,
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId,
            @Valid @RequestBody SubmitChallengeReqDto submitChallengeReqDto);

    @Operation(summary = "오늘의 챌린지 리셋", description = "오늘의 챌린지를 리셋합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "오늘의 챌린지 리셋 성공")
    })
    ResponseEntity<RspTemplate<DailyChallengesResDto>> resetTodaysChallenges(@AuthenticatedEmail String email);

}
