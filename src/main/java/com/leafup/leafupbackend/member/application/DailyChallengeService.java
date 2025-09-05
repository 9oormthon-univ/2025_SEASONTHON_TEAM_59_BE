package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import com.leafup.leafupbackend.challenge.domain.repository.ChallengeRepository;
import com.leafup.leafupbackend.member.api.dto.response.DailyChallengeResDto;
import com.leafup.leafupbackend.member.api.dto.response.DailyChallengesResDto;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.DailyMemberChallengeImage;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeImageRepository;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.ChallengeOwnershipException;
import com.leafup.leafupbackend.member.exception.DailyMemberChallengeNotFoundException;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyChallengeService {

    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final DailyMemberChallengeImageRepository dailyMemberChallengeImageRepository;
    private final LevelService levelService;

    private static final Set<ChallengeStatus> EXCLUDED_STATUSES_FOR_ADVANCED = Set.of(
            ChallengeStatus.PENDING_APPROVAL,
            ChallengeStatus.COMPLETED,
            ChallengeStatus.REJECTED
    );

    @Transactional
    public DailyChallengesResDto getOrCreateTodaysChallenges(String email) {
        Member member = findMemberByEmail(email);
        LocalDate today = LocalDate.now();

        List<DailyMemberChallenge> todaysChallenges = dailyMemberChallengeRepository.
                findByMemberAndChallengeDate(member, today);

        // 오늘 일일 챌린지가 비었다면 새로 생성
        if (todaysChallenges.isEmpty()) {
            updateStageBasedOnSubmissionOrder(member);

            todaysChallenges = createNewDailyChallenges(member, today);
        }

        // 챌린지 타입이 어려움이고, 챌린지 상태가 도전가능이 아니면 제외
        // 즉, 챌린지 타입이 어려움인데, 대기, 완료, 반려 상태라면 제외한다.
        // 고급 챌린지 인증 요청하고 뒤로 빼둔 후, 새로운 중간 챌린지를 그 자리에 채워주기 위함.
        List<DailyChallengeResDto> filteredChallenges = todaysChallenges.stream()
                .filter(dmc -> {
                    boolean isAdvanced = dmc.getChallenge().getChallengeType() == ChallengeType.HARD;
                    boolean isExcludedStatus = EXCLUDED_STATUSES_FOR_ADVANCED.contains(dmc.getChallengeStatus());

                    return !(isAdvanced && isExcludedStatus);
                })
                .map(dmc -> {
                    return DailyChallengeResDto.of(dmc.getId(),
                            dmc.getChallenge().getContents(),
                            dmc.getChallenge().getChallengeType(),
                            dmc.getChallengeStatus());
                })
                .toList();

        // 인증이 완료된 챌린지를 카운트한다. 메인페이지의 깃발을 위함. 포인트 상승은 관리자 승인시 진행될 예정.
        // 초기 단계 에서는 관리자 승인이 아니라 대기여도 카운트 되어야 할 수도 있음. (수정 가능성 높음)
        int completedCount = dailyMemberChallengeRepository
                .countByMemberAndChallengeDateAndChallengeStatus(member, today, ChallengeStatus.COMPLETED);

        return DailyChallengesResDto.of(
                member.getCurrentStage(),
                completedCount,
                filteredChallenges);
    }

    @Transactional
    public void submitChallenge(String email, Long dailyMemberChallengeId, String imageUrl) {
        Member member = findMemberByEmail(email);
        DailyMemberChallenge dailyMemberChallenge = findDailyChallengeById(dailyMemberChallengeId);

        // 본인의 일일 챌린지가 아니면 예외
        if (!dailyMemberChallenge.getMember().equals(member)) {
            throw new ChallengeOwnershipException();
        }

        // 챌린지를 대기 상태로 수정 후, 챌린지 이미지 저장
        dailyMemberChallenge.updateChallengeStatus(ChallengeStatus.PENDING_APPROVAL);
        DailyMemberChallengeImage dailyMemberChallengeImage = DailyMemberChallengeImage.builder()
                .dailyMemberChallenge(dailyMemberChallenge)
                .imageUrl(imageUrl)
                .build();

        dailyMemberChallengeImageRepository.save(dailyMemberChallengeImage);

        // 챌린지 타입에 맞는 포인트를 사용자에게 추가와 LevelUp, exp 업데이트, 스테이지 + 1 (도전가능 상태만 아니면 상승)
        levelService.addPointAndHandleLevelUpAndExp(member,
                dailyMemberChallenge.getChallenge().getChallengeType().getPoint(),
                "데일리 챌린지 인증");
        member.plusStage();

        // 스트릭 업데이트
        updateStreak(member);

        replaceHardChallengeIfCompleted(dailyMemberChallenge);
    }

    private void updateStreak(Member member) {
        LocalDate today = LocalDate.now();
        LocalDate lastUpdate = member.getLastStreakUpdatedAt();

        if (lastUpdate == null) { // 첫 활동
            member.resetStreakToOne();
        } else if (lastUpdate.isEqual(today.minusDays(1))) { // 연속 활동
            member.incrementStreak();
        } else if (lastUpdate.isBefore(today.minusDays(1))) { // 연속 끊김
            member.resetStreakToOne();
        } else { // 같은 날 재활동
            return; // 아무것도 하지 않음
        }

        member.updateLastStreakUpdatedAt(today);
    }

    private void replaceHardChallengeIfCompleted(DailyMemberChallenge completedChallenge) {
        if (completedChallenge.getChallenge().getChallengeType() != ChallengeType.HARD) {
            return;
        }

        // 챌린지 타입 고급 요청이 들어오면 중급 타입 챌린지 추가
        Member member = completedChallenge.getMember();
        LocalDate today = completedChallenge.getChallengeDate();

        Set<Long> todaysChallengeIds = dailyMemberChallengeRepository.findByMemberAndChallengeDate(member, today).stream()
                .map(dmc -> dmc.getChallenge().getId())
                .collect(Collectors.toSet());

        Challenge newMediumChallenge = challengeRepository.findAll().stream()
                .filter(c -> c.getChallengeType() == ChallengeType.MEDIUM && !todaysChallengeIds.contains(c.getId()))
                .findAny()
                .orElse(null);

        if (newMediumChallenge != null) {
            DailyMemberChallenge newDmc = DailyMemberChallenge.builder()
                    .member(member)
                    .challenge(newMediumChallenge)
                    .challengeDate(today)
                    .build();

            dailyMemberChallengeRepository.save(newDmc);
        }
    }

    // 챌린지 반려 되었을 때 해당 스테이지로 현재 스테이지 수정
    private void updateStageBasedOnSubmissionOrder(Member member) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<DailyMemberChallenge> yesterdaysDmcs = dailyMemberChallengeRepository
                .findByMemberAndChallengeDate(member, yesterday);

        if (yesterdaysDmcs.isEmpty()) {
            return;
        }

        List<DailyMemberChallengeImage> yesterdaysImages = dailyMemberChallengeImageRepository
                .findByDailyMemberChallengeIn(yesterdaysDmcs);

        if (yesterdaysImages.isEmpty()) {
            return;
        }

        yesterdaysImages.sort(Comparator.comparing(DailyMemberChallengeImage::getId));

        int revertStage = -1;

        for (int i = 0; i < yesterdaysImages.size(); i++) {
            DailyMemberChallengeImage image = yesterdaysImages.get(i);
            if (image.getDailyMemberChallenge().getChallengeStatus() == ChallengeStatus.REJECTED) {
                revertStage = i + 1;
                break;
            }
        }

        if (revertStage != -1) {
            member.updateCurrentStage(revertStage);
            memberRepository.save(member);
        }
    }

    private List<DailyMemberChallenge> createNewDailyChallenges(Member member, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        Set<Long> challengesToExclude = dailyMemberChallengeRepository
                .findChallengeIdsByMemberAndChallengeDate(member, yesterday);

        List<Challenge> availableChallengesSource;
        if (challengesToExclude.isEmpty()) {
            // 어제 챌린지가 비었으면 챌린지 목록을 모두 불러와서 저장
            availableChallengesSource = challengeRepository.findAll();
        } else {
            // 어제 챌린지가 존재한다면 어제 챌린지들을 제외하고 남은 챌린지들 저장
            availableChallengesSource = challengeRepository.findChallengesToSelect(challengesToExclude);
        }

        Map<ChallengeType, List<Challenge>> availableChallenges = availableChallengesSource.stream()
                .filter(c -> !challengesToExclude.contains(c.getId()))
                .collect(Collectors.groupingBy(Challenge::getChallengeType));

        List<Challenge> selected = new ArrayList<>();
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.EASY, Collections.emptyList()), 2));
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.MEDIUM, Collections.emptyList()), 2));
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.HARD, Collections.emptyList()), 1));

        List<DailyMemberChallenge> newChallenges = new ArrayList<>();
        for (Challenge challenge : selected) {
            DailyMemberChallenge dailyMemberChallenge = DailyMemberChallenge.builder()
                    .member(member)
                    .challenge(challenge)
                    .challengeDate(today)
                    .build();

            newChallenges.add(dailyMemberChallenge);
        }

        dailyMemberChallengeRepository.saveAll(newChallenges);
        return newChallenges;
    }

    private List<Challenge> selectRandomChallenges(List<Challenge> challenges, int count) {
        if (challenges.size() < count) {
            return challenges;
        }

        Collections.shuffle(challenges);
        return challenges.subList(0, count);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private DailyMemberChallenge findDailyChallengeById(Long id) {
        return dailyMemberChallengeRepository.findById(id).orElseThrow(DailyMemberChallengeNotFoundException::new);
    }

}
