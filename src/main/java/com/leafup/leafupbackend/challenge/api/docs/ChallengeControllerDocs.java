package com.leafup.leafupbackend.challenge.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.challenge.api.dto.response.ChallengesResDto;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface ChallengeControllerDocs {

    @Operation(summary = "난이도별 챌린지 조회", description = "난이도별 챌린지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "난이도별 챌린지 조회 성공")
    })
    ResponseEntity<RspTemplate<ChallengesResDto>> getChallenges(
            @RequestParam(name = "type", required = false) ChallengeType type);

}
