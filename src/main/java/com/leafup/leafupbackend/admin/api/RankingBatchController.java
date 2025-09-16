package com.leafup.leafupbackend.admin.api;

import com.leafup.leafupbackend.ranking.application.batch.RankingBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin Batch", description = "관리자용 배치 작업 수동 실행 API")
@RestController
@RequestMapping("/api/v1/admin/batch")
@RequiredArgsConstructor
public class RankingBatchController {

    private final RankingBatchService rankingBatchService;

    @Operation(summary = "현재 월간 랭킹 집계 수동 실행 (테스트용)")
    @PostMapping("/rankings/monthly")
    public ResponseEntity<String> triggerMonthlyRankingAggregation() {
        rankingBatchService.triggerCurrentMonthRankingAggregation();
        return ResponseEntity.ok("현재 월간 랭킹 집계 배치가 성공적으로 실행되었습니다.");
    }
}