package com.leafup.leafupbackend.ranking.application;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.point.application.dto.PointAggregationDto;
import com.leafup.leafupbackend.point.domain.repository.PointHistoryRepository;
import com.leafup.leafupbackend.ranking.api.dto.response.MyRankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingsResDto;
import com.leafup.leafupbackend.ranking.domain.MonthlyRanking;
import com.leafup.leafupbackend.ranking.domain.repository.MonthlyRankingRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final MonthlyRankingRepository monthlyRankingRepository;

    public RankingsResDto getTotalRanking() {
        List<PointAggregationDto> totalPointsByMember = pointHistoryRepository.findTotalPointsByMember();
        return createRankingsFrom(totalPointsByMember, PointAggregationDto::member, PointAggregationDto::totalPoints);
    }

    public RankingsResDto getStreakRanking() {
        List<Member> members = memberRepository.findTop100ByOrderByStreakDesc();
        return createRankingsFrom(members, Function.identity(), member -> (long) member.getStreak());
    }

    public RankingsResDto getMonthlyRegionalRanking(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String region = member.getAddress();

        List<MonthlyRanking> monthlyRankings = monthlyRankingRepository
                .findByYearAndMonthAndRegionOrderByPointDesc(year, month, region);

        return createRankingsFrom(monthlyRankings, MonthlyRanking::getMember, ranking -> (long) ranking.getPoint());
    }

    public MyRankingResDto getMyTotalRanking(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        long myTotalPoints = pointHistoryRepository.findTotalPointsBySingleMember(member)
                .orElse(0L);

        long higherRankCount = pointHistoryRepository.findMembersWithHigherTotalPoints(myTotalPoints).size();

        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank,
                member.getNickname(),
                member.getCode(),
                member.getPicture(), myTotalPoints);
    }

    public MyRankingResDto getMyStreakRanking(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        int myStreak = member.getStreak();
        long higherRankCount = memberRepository.countByStreakGreaterThan(myStreak);
        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank, member.getNickname(), member.getCode(), member.getPicture(),
                myStreak);
    }

    public MyRankingResDto getMyMonthlyRegionalRanking(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String region = member.getAddress();

        return monthlyRankingRepository
                .findByYearAndMonthAndRegionAndMember(year, month, region, member)
                .map(myMonthlyRanking -> {
                    long higherRankCount = monthlyRankingRepository
                            .countByYearAndMonthAndRegionAndPointGreaterThan(year, month, region,
                                    myMonthlyRanking.getPoint());
                    long myRank = higherRankCount + 1;
                    return MyRankingResDto.of(myRank,
                            member.getNickname(),
                            member.getCode(),
                            member.getPicture(),
                            myMonthlyRanking.getPoint());
                })
                .orElse(MyRankingResDto.of(0, member.getNickname(), member.getCode(), member.getPicture(), 0));
    }

    private <T> RankingsResDto createRankingsFrom(List<T> items, Function<T, Member> memberExtractor,
                                                  Function<T, Long> scoreExtractor) {
        List<RankingResDto> rankings = new java.util.ArrayList<>();
        long lastScore = -1;
        int lastRank = 0;

        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            Member member = memberExtractor.apply(item);
            long currentScore = scoreExtractor.apply(item);
            int currentRank;

            if (i > 0 && currentScore == lastScore) {
                currentRank = lastRank;
            } else {
                currentRank = i + 1;
            }

            rankings.add(RankingResDto.of(
                    currentRank,
                    member.getNickname(),
                    member.getCode(),
                    member.getPicture(),
                    currentScore));
            lastScore = currentScore;
            lastRank = currentRank;
        }
        return RankingsResDto.of(rankings);
    }

}
