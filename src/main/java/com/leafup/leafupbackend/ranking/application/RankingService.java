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
    private final MonthlyRankingRepository monthlyRankingRepository;

    public RankingsResDto getTotalRanking() {
        List<Member> members = memberRepository.findTop100ByOrderByPointDesc();
        return createRankingsFrom(members, member -> (long) member.getPoint());
    }

    public RankingsResDto getStreakRanking() {
        List<Member> members = memberRepository.findTop100ByOrderByStreakDesc();
        return createRankingsFrom(members, member -> (long) member.getStreak());
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
                        ranking.getMember().getCode(),
                        ranking.getMember().getPicture(),
                        ranking.getPoint()))
                .toList();

        return RankingsResDto.of(rankings);
    }

    public MyRankingResDto getMyTotalRanking(String email) {
        return getMyRanking(email, Member::getPoint, memberRepository::countByPointGreaterThan);
    }

    public MyRankingResDto getMyStreakRanking(String email) {
        return getMyRanking(email, Member::getStreak, memberRepository::countByStreakGreaterThan);
    }

    public MyRankingResDto getMyMonthlyRegionalRanking(String email, int year, int month) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String region = member.getAddress();

        Optional<MonthlyRanking> myMonthlyRankingOpt = monthlyRankingRepository
                .findByYearAndMonthAndRegionAndMember(year, month, region, member);

        if (myMonthlyRankingOpt.isEmpty()) {
            return MyRankingResDto.of(0, member.getNickname(), member.getCode(), member.getPicture(), 0);
        }

        MonthlyRanking myMonthlyRanking = myMonthlyRankingOpt.get();
        long higherRankCount = monthlyRankingRepository
                .countByYearAndMonthAndRegionAndPointGreaterThan(year, month, region, myMonthlyRanking.getPoint());
        long myRank = higherRankCount + 1;

        return MyRankingResDto.of(myRank,
                member.getNickname(),
                member.getCode(),
                member.getPicture(),
                myMonthlyRanking.getPoint());
    }

    private RankingsResDto createRankingsFrom(List<Member> members, Function<Member, Long> scoreExtractor) {
        AtomicInteger rank = new AtomicInteger(1);
        List<RankingResDto> rankings = members.stream()
                .map(member -> RankingResDto.of(
                        rank.getAndIncrement(),
                        member.getNickname(),
                        member.getCode(),
                        member.getPicture(),
                        scoreExtractor.apply(member)
                ))
                .toList();
        return RankingsResDto.of(rankings);
    }

    private MyRankingResDto getMyRanking(String email, ToIntFunction<Member> scoreExtractor,
                                         ToLongFunction<Integer> rankCounter) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        int score = scoreExtractor.applyAsInt(member);
        long higherRankCount = rankCounter.applyAsLong(score);
        long myRank = higherRankCount + 1;
        return MyRankingResDto.of(myRank, member.getNickname(), member.getCode(), member.getPicture(), score);
    }

}
