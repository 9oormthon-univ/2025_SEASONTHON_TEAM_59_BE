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
        processMonthlyRanking(lastMonth.getYear(), lastMonth.getMonthValue());
    }

    /**
     * [테스트용] 현재 시점의 월간 랭킹을 집계하여 저장합니다. 이 메서드는 API를 통해 수동으로 호출됩니다.
     */
    @Transactional
    public void triggerCurrentMonthRankingAggregation() {
        LocalDate thisMonth = LocalDate.now();
        processMonthlyRanking(thisMonth.getYear(), thisMonth.getMonthValue());
    }

    private void processMonthlyRanking(int year, int month) {
        log.info("월간 랭킹 집계를 시작합니다. ({}년 {}월)", year, month);

        if (monthlyRankingRepository.existsByYearAndMonth(year, month)) {
            log.warn("{}년 {}월의 랭킹 데이터가 이미 존재하여, 기존 데이터를 삭제하고 다시 집계합니다.", year, month);
            monthlyRankingRepository.deleteByYearAndMonth(year, month);
        }

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
