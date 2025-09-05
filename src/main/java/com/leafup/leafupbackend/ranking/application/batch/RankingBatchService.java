package com.leafup.leafupbackend.ranking.application.batch;

import com.leafup.leafupbackend.point.application.dto.MonthlyPointAggregationDto;
import com.leafup.leafupbackend.point.domain.repository.PointHistoryRepository;
import com.leafup.leafupbackend.ranking.domain.MonthlyRanking;
import com.leafup.leafupbackend.ranking.domain.repository.MonthlyRankingRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RankingBatchService {

    private final PointHistoryRepository pointHistoryRepository;
    private final MonthlyRankingRepository monthlyRankingRepository;

    /**
     * 매월 1일 새벽 2시에 지난달의 월간 랭킹을 집계하여 저장합니다.
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    @Transactional
    public void aggregateAndSaveMonthlyRankings() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();

        log.info("월간 랭킹 집계를 시작합니다. ({}년 {}월)", year, month);

        List<MonthlyPointAggregationDto> monthlyPoints = pointHistoryRepository.aggregateMonthlyPoints(year, month);

        List<MonthlyRanking> monthlyRankings = monthlyPoints.stream()
                .map(dto -> MonthlyRanking.builder()
                        .member(dto.member())
                        .year(year)
                        .month(month)
                        .region(dto.member().getAddress())
                        .point((int) dto.totalPoints())
                        .build())
                .toList();

        monthlyRankingRepository.saveAll(monthlyRankings);

        log.info("월간 랭킹 집계를 완료했습니다. 총 {}개의 랭킹이 저장되었습니다.", monthlyRankings.size());
    }
}
