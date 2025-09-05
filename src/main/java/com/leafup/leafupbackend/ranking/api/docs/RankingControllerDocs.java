package com.leafup.leafupbackend.ranking.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.ranking.api.dto.response.MyRankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

public interface RankingControllerDocs {

    @Operation(summary = "전체 누적 포인트 랭킹 조회", description = "전체 사용자의 누적 포인트 기준 랭킹을 상위 100위까지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 누적 포인트 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<RankingsResDto>> getTotalRanking();

    @Operation(summary = "전체 스트릭 랭킹 조회", description = "전체 사용자의 스트릭 기준 랭킹을 상위 100위까지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 스트릭 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<RankingsResDto>> getStreakRanking();

    @Operation(summary = "월간 지역 랭킹 조회", description = "로그인한 사용자의 지역을 기준으로 월간 랭킹을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "월간 지역 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<RankingsResDto>> getMonthlyRegionalRanking(@AuthenticatedEmail String email,
                                                                          @RequestParam int year,
                                                                          @RequestParam int month);

    @Operation(summary = "나의 전체 누적 포인트 랭킹 조회", description = "나의 전체 누적 포인트 랭킹을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 전체 누적 포인트 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<MyRankingResDto>> getMyTotalRanking(@AuthenticatedEmail String email);

    @Operation(summary = "나의 전체 스트릭 랭킹 조회", description = "나의 전체 스트릭 랭킹을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 전체 스트릭 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<MyRankingResDto>> getMyStreakRanking(@AuthenticatedEmail String email);

    @Operation(summary = "나의 월간 지역 랭킹 조회", description = "나의 월간 지역 랭킹을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "나의 월간 지역 랭킹 조회 성공")
    })
    ResponseEntity<RspTemplate<MyRankingResDto>> getMyMonthlyRegionalRanking(@AuthenticatedEmail String email,
                                                                             @RequestParam int year,
                                                                             @RequestParam int month);

}