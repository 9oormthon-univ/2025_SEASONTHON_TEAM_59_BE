package com.leafup.leafupbackend.statistics.application;

import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.statistics.api.dto.response.GlobalStatisticsResDto;
import com.leafup.leafupbackend.statistics.api.dto.response.MyStatisticsResDto;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private static final double TREE_CONVERSION_FACTOR = 125000.0;
    private static final double CAR_EMISSION_CONVERSION_FACTOR = 1335000.0;

    private final MemberRepository memberRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;

    public MyStatisticsResDto getMyStatistics(String email) {
        Member member = findMemberByEmail(email);

        double totalCarbonReduction = member.getCarbonReduction();

        double treesPlantedEffect = totalCarbonReduction / TREE_CONVERSION_FACTOR;
        double carEmissionReductionEffect = totalCarbonReduction / CAR_EMISSION_CONVERSION_FACTOR;

        return MyStatisticsResDto.of(totalCarbonReduction, treesPlantedEffect, carEmissionReductionEffect);
    }

    public GlobalStatisticsResDto getGlobalStatistics() {
        double totalCarbonReduction = dailyMemberChallengeRepository
                .sumTotalCarbonReductionForAllUsers(ChallengeStatus.COMPLETED);

        long totalMemberCount = memberRepository.count();
        Optional<LocalDate> serviceStartDateOpt = dailyMemberChallengeRepository.findMinChallengeDate();
        long serviceOperatingDays = serviceStartDateOpt
                .map(startDate -> ChronoUnit.DAYS.between(startDate, LocalDate.now()) + 1)
                .orElse(0L);

        double dailyAverageReduction = (serviceOperatingDays > 0) ? totalCarbonReduction / serviceOperatingDays : 0.0;

        double treesPlantedEffect = totalCarbonReduction / TREE_CONVERSION_FACTOR;
        double carEmissionReductionEffect = totalCarbonReduction / CAR_EMISSION_CONVERSION_FACTOR;

        return GlobalStatisticsResDto.of(
                totalCarbonReduction,
                totalMemberCount,
                dailyAverageReduction,
                treesPlantedEffect,
                carEmissionReductionEffect
        );
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

}
