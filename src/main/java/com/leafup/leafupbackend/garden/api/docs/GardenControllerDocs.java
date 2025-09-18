package com.leafup.leafupbackend.garden.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.garden.api.dto.response.WeeklyGardenResDto;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface GardenControllerDocs {

    @Operation(summary = "주간 텃밭 현황 조회", description = "로그인한 사용자의 현재 주차 텃밭 현황(완료한 챌린지 목록)을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주간 텃밭 현황 조회 성공")
    })
    ResponseEntity<RspTemplate<WeeklyGardenResDto>> getWeeklyGardenStatus(@AuthenticatedEmail String email);

    @Operation(summary = "주간 텃밭 열매 수확", description = "주간 텃밭에서 열매를 수확하여 9포인트를 획득합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주간 텃밭 열매 수확 성공")
    })
    ResponseEntity<RspTemplate<Void>> harvestFromGarden(@AuthenticatedEmail String email,
                                                        @PathVariable("challengeId") Long challengeId);

}
