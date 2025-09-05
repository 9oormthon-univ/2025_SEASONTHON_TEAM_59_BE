package com.leafup.leafupbackend.ranking.application;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.ranking.api.dto.response.MyRankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingResDto;
import com.leafup.leafupbackend.ranking.api.dto.response.RankingsResDto;
import com.leafup.leafupbackend.ranking.domain.MonthlyRanking;
import com.leafup.leafupbackend.ranking.domain.repository.MonthlyRankingRepository;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {

    private final MemberRepository memberRepository;
    private final MonthlyRankingRepository monthlyRankingRepository;

    public RankingsResDto getTotalRanking() {
        List<Member> members = memberRepository.findTop100ByOrderByPointDesc();

        AtomicInteger rank = new AtomicInteger(1);
        List<RankingResDto> rankings = members.stream()
                .map(member -> RankingResDto.of(
                        rank.getAndIncrement(),
                        member.getNickname(),
                        member.getPicture(),
                        member.getPoint()))
                .toList();

        return RankingsResDto.of(rankings);
    }

    public RankingsResDto getStreakRanking() {
        List<Member> members = memberRepository.findTop100ByOrderByStreakDesc();

        AtomicInteger rank = new AtomicInteger(1);
        List<RankingResDto> rankings = members.stream()
                .map(member -> RankingResDto.of(
                        rank.getAndIncrement(),
                        member.getNickname(),
                        member.getPicture(),
                        member.getStreak()))
                .toList();

        return RankingsResDto.of(rankings);
    }

    public RankingsResDto getMonthlyRegionalRanking(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String region = member.getAddress();

        List<MonthlyRanking> monthlyRankings = monthlyRankingRepository
                .findByYearAndMonthAndRegionOrderByPointDesc(year, month, region);

        AtomicInteger rank = new AtomicInteger(1);
        List<RankingResDto> rankings = monthlyRankings.stream()
                .map(ranking -> RankingResDto.of(
                        rank.getAndIncrement(),
                        ranking.getMember().getNickname(),
                        ranking.getMember().getPicture(),
                        ranking.getPoint()))
                .toList();

        return RankingsResDto.of(rankings);
    }

    public MyRankingResDto getMyTotalRanking(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        long higherRankCount = memberRepository.countByPointGreaterThan(member.getPoint());
        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank,
                member.getNickname(),
                member.getPicture(),
                member.getPoint());
    }

    public MyRankingResDto getMyStreakRanking(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        long higherRankCount = memberRepository.countByStreakGreaterThan(member.getStreak());
        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank,
                member.getNickname(),
                member.getPicture(),
                member.getStreak());
    }

    public MyRankingResDto getMyMonthlyRegionalRanking(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String region = member.getAddress();

        Optional<MonthlyRanking> myMonthlyRankingOpt = monthlyRankingRepository
                .findByYearAndMonthAndRegionAndMember(year, month, region, member);

        // 해당 월에 랭킹 기록이 없는 경우
        if (myMonthlyRankingOpt.isEmpty()) {
            return MyRankingResDto.of(0, member.getNickname(), member.getPicture(), 0);
        }

        MonthlyRanking myMonthlyRanking = myMonthlyRankingOpt.get();
        long higherRankCount = monthlyRankingRepository
                .countByYearAndMonthAndRegionAndPointGreaterThan(year, month, region, myMonthlyRanking.getPoint());
        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank, member.getNickname(), member.getPicture(), myMonthlyRanking.getPoint());
    }

}
