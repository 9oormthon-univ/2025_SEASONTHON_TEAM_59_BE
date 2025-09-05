package com.leafup.leafupbackend.admin.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface AdminControllerDocs {

    @Operation(summary = "(관리자) 챌린지 승인", description = "관리자가 챌린지를 승인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "승인 성공")
    })
    ResponseEntity<RspTemplate<Void>> approveChallenge(
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId);

    @Operation(summary = "(관리자) 챌린지 반려", description = "관리자가 챌린지를 반려합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "반려 성공")
    })
    ResponseEntity<RspTemplate<Void>> rejectChallenge(
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId);

}
