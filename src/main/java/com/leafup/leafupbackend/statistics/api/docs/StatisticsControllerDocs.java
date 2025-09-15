package com.leafup.leafupbackend.statistics.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.statistics.api.dto.response.GlobalStatisticsResDto;
import com.leafup.leafupbackend.statistics.api.dto.response.MyStatisticsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

public interface StatisticsControllerDocs {

    @Operation(summary = "나의 탄소 감축량 통계 조회", description = "나의 탄소 감축량 통계를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 탄소 감축량 통계 조회 성공")
    })
    ResponseEntity<RspTemplate<MyStatisticsResDto>> getMyStatistics(@AuthenticatedEmail String email);

    @Operation(summary = "전체 탄소 감축량 통계 조회", description = "전체 탄소 감축량 통계 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 탄소 감축량 통계 조회 성공")
    })
    ResponseEntity<RspTemplate<GlobalStatisticsResDto>> getGlobalStatistics();

}
