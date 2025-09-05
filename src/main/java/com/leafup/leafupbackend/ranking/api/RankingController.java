package com.leafup.leafupbackend.ranking.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.ranking.api.docs.RankingControllerDocs;
import com.leafup.leafupbackend.ranking.api.dto.response.MyRankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingsResDto;
import com.leafup.leafupbackend.ranking.application.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ranking")
public class RankingController implements RankingControllerDocs {

    private final RankingService rankingService;

    @Override
    @GetMapping("/total")
    public ResponseEntity<RspTemplate<RankingsResDto>> getTotalRanking() {
        return RspTemplate.<RankingsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("전체 누적 포인트 랭킹 조회 성공")
                .data(rankingService.getTotalRanking())
                .build()
                .toResponseEntity();
    }

    @Override
    @GetMapping("/streak")
    public ResponseEntity<RspTemplate<RankingsResDto>> getStreakRanking() {
        return RspTemplate.<RankingsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("전체 스트릭 랭킹 조회 성공")
                .data(rankingService.getStreakRanking())
                .build()
                .toResponseEntity();
    }

    @Override
    @GetMapping("/monthly/regional")
    public ResponseEntity<RspTemplate<RankingsResDto>> getMonthlyRegionalRanking(@AuthenticatedEmail String email,
                                                                                 @RequestParam int year,
                                                                                 @RequestParam int month) {
        return RspTemplate.<RankingsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("월간 지역 랭킹 조회 성공")
                .data(rankingService.getMonthlyRegionalRanking(email, year, month))
                .build()
                .toResponseEntity();
    }

    @Override
    @GetMapping("/me/total")
    public ResponseEntity<RspTemplate<MyRankingResDto>> getMyTotalRanking(@AuthenticatedEmail String email) {
        return RspTemplate.<MyRankingResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("나의 전체 누적 포인트 랭킹 조회 성공")
                .data(rankingService.getMyTotalRanking(email))
                .build()
                .toResponseEntity();
    }

    @Override
    @GetMapping("/me/streak")
    public ResponseEntity<RspTemplate<MyRankingResDto>> getMyStreakRanking(@AuthenticatedEmail String email) {
        return RspTemplate.<MyRankingResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("나의 전체 스트릭 랭킹 조회 성공")
                .data(rankingService.getMyStreakRanking(email))
                .build()
                .toResponseEntity();
    }

    @Override
    @GetMapping("/me/monthly/regional")
    public ResponseEntity<RspTemplate<MyRankingResDto>> getMyMonthlyRegionalRanking(@AuthenticatedEmail String email,
                                                                                    @RequestParam int year,
                                                                                    @RequestParam int month) {
        return RspTemplate.<MyRankingResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("나의 월간 지역 랭킹 조회 성공")
                .data(rankingService.getMyMonthlyRegionalRanking(email, year, month))
                .build()
                .toResponseEntity();
    }

}
