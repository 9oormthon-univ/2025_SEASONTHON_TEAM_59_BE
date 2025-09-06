package com.leafup.leafupbackend.admin.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.admin.api.dto.response.PendingChallengesResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "관리자", description = "관리자 기능 관련 API")
public interface AdminControllerDocs {

    @Operation(summary = "승인 대기 중인 챌린지 목록 조회", description = "관리자가 승인/반려해야 할 챌린지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 목록 조회 성공")
    })
    ResponseEntity<RspTemplate<PendingChallengesResDto>> getPendingChallenges();

    @Operation(summary = "챌린지 승인", description = "사용자가 제출한 챌린지를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 승인 성공")
    })
    ResponseEntity<RspTemplate<Void>> approveChallenge(@PathVariable Long dailyMemberChallengeId);

    @Operation(summary = "챌린지 반려", description = "사용자가 제출한 챌린지를 반려합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "챌린지 반려 성공")
    })
    ResponseEntity<RspTemplate<Void>> rejectChallenge(@PathVariable Long dailyMemberChallengeId);
}
