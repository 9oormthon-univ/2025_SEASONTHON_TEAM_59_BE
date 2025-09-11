package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import com.leafup.leafupbackend.challenge.domain.repository.ChallengeRepository;
import com.leafup.leafupbackend.global.cache.DailyChallengeCacheService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyChallengeService {

    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final DailyMemberChallengeImageRepository dailyMemberChallengeImageRepository;
    private final DailyChallengeCacheService dailyChallengeCacheService;

    @Transactional
    public DailyChallengesResDto getOrCreateTodaysChallenges(String email) {
        Member member = findMemberByEmail(email);
        LocalDate today = LocalDate.now();
        int currentStage = member.getCurrentStage();

        Optional<DailyChallengesResDto> cachedChallenges = dailyChallengeCacheService.getCachedChallenges(email, today, currentStage);
        if (cachedChallenges.isPresent()) {
            return cachedChallenges.get();
        }

        DailyChallengesResDto todaysChallengesDto = getChallengesFromDb(member, today);
        dailyChallengeCacheService.cacheChallenges(email, today, currentStage, todaysChallengesDto);

        return todaysChallengesDto;
    }

    @Transactional
    public void submitChallenge(String email, Long dailyMemberChallengeId, String imageUrl) {
        Member member = findMemberByEmail(email);
        DailyMemberChallenge dailyMemberChallenge = findDailyChallengeById(dailyMemberChallengeId);

        if (!dailyMemberChallenge.getMember().equals(member)) {
            throw new ChallengeOwnershipException();
        }

        dailyChallengeCacheService.deleteDailyChallengeCache(email, LocalDate.now(), member.getCurrentStage());

        DailyMemberChallengeImage dailyMemberChallengeImage = DailyMemberChallengeImage.builder()
                .dailyMemberChallenge(dailyMemberChallenge)
                .imageUrl(imageUrl)
                .build();
        dailyMemberChallengeImageRepository.save(dailyMemberChallengeImage);

        boolean isTwoCut = dailyMemberChallenge.getChallenge().isTwoCut();
        int imageCount = isTwoCut ? dailyMemberChallengeImageRepository.countByDailyMemberChallenge(dailyMemberChallenge) : 1;

        if ((isTwoCut && imageCount == 2) || !isTwoCut) {
            dailyMemberChallenge.updateChallengeStatus(ChallengeStatus.PENDING_APPROVAL);
            expireOtherActiveChallenges(member, LocalDate.now(), dailyMemberChallengeId);
            member.plusStage();
            updateStreak(member);
            dailyChallengeCacheService.deleteDailyChallengeCache(email, LocalDate.now(), member.getCurrentStage());

            if (dailyMemberChallenge.getChallenge().getChallengeType() == ChallengeType.HARD) {
                createBonusDailyChallenges(member, LocalDate.now());
            } else {
                createNewChallenges(member, LocalDate.now(), 2, 2, 0);
            }
        }
    }

    @Transactional
    public DailyChallengesResDto resetTodaysChallenges(String email) {
        Member member = findMemberByEmail(email);
        LocalDate today = LocalDate.now();

        List<DailyMemberChallenge> todaysChallenges = dailyMemberChallengeRepository.findByMemberAndChallengeDate(member, today);
        if (!todaysChallenges.isEmpty()) {
            todaysChallenges.forEach(challenge -> challenge.updateChallengeStatus(ChallengeStatus.EXPIRED));
        }

        dailyChallengeCacheService.deleteDailyChallengeCache(email, today, member.getCurrentStage());

        createNewChallenges(member, today, 2, 2, 1);

        return getChallengesFromDb(member, today);
    }

    private DailyChallengesResDto getChallengesFromDb(Member member, LocalDate today) {
        updateStageBasedOnSubmissionOrder(member, today);

        List<DailyMemberChallenge> todaysAllChallenges = dailyMemberChallengeRepository
                .findByMemberAndChallengeDate(member, today);

        if (todaysAllChallenges.isEmpty()) {
            todaysAllChallenges = createNewDailyChallenges(member, today);
        }

        List<DailyChallengeResDto> challengeDtos = todaysAllChallenges.stream()
                .filter(c -> c.getChallengeStatus() == ChallengeStatus.ACTIVE)
                .sorted(Comparator.comparing(DailyMemberChallenge::getId))
                .map(dmc -> DailyChallengeResDto.of(dmc.getId(),
                        dmc.getChallenge().getContents(),
                        dmc.getChallenge().getChallengeType(),
                        dmc.getChallengeStatus(), dmc.getChallenge().isTwoCut()))
                .toList();

        int completedCount = (int) todaysAllChallenges.stream()
                .filter(c -> c.getChallengeStatus() == ChallengeStatus.COMPLETED)
                .count();

        List<DailyMemberChallenge> finishedStageChallenges = todaysAllChallenges.stream()
                .filter(c -> c.getChallengeStatus() == ChallengeStatus.COMPLETED ||
                        c.getChallengeStatus() == ChallengeStatus.PENDING_APPROVAL ||
                        c.getChallengeStatus() == ChallengeStatus.REJECTED)
                .sorted(Comparator.comparing(DailyMemberChallenge::getId))
                .toList();

        List<String> stageStatus = new ArrayList<>();
        for (DailyMemberChallenge challenge : finishedStageChallenges) {
            stageStatus.add(switch (challenge.getChallengeStatus()) {
                case COMPLETED -> "approved";
                case REJECTED -> "rejected";
                case PENDING_APPROVAL -> "pending";
                default -> "unknown";
            });
        }
        while (stageStatus.size() < 5) {
            stageStatus.add("active");
        }
        if (stageStatus.size() > 5) {
            stageStatus = stageStatus.subList(0, 5);
        }

        return DailyChallengesResDto.of(member.getCurrentStage(), completedCount, stageStatus, challengeDtos);
    }

    private void updateStageBasedOnSubmissionOrder(Member member, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
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
            if (yesterdaysImages.get(i).getDailyMemberChallenge().getChallengeStatus() == ChallengeStatus.REJECTED) {
                revertStage = i + 1;
                break;
            }
        }

        if (revertStage != -1) {
            member.updateCurrentStage(revertStage);
        }
    }

    private List<DailyMemberChallenge> createNewDailyChallenges(Member member, LocalDate today) {
        return createNewChallenges(member, today, 2, 2, 1);
    }

    private void createBonusDailyChallenges(Member member, LocalDate today) {
        createNewChallenges(member, today, 2, 3, 0);
    }

    private List<DailyMemberChallenge> createNewChallenges(Member member, LocalDate today, int easyCount, int mediumCount, int hardCount) {
        Set<Long> challengesToExclude = dailyMemberChallengeRepository.findChallengeIdsByMemberAndChallengeDateAndChallengeType(member, today, ChallengeType.HARD);
        List<Challenge> availableChallengesSource = challengeRepository.findAll();

        Map<ChallengeType, List<Challenge>> availableChallenges = availableChallengesSource.stream()
                .filter(c -> !challengesToExclude.contains(c.getId()))
                .collect(Collectors.groupingBy(Challenge::getChallengeType));

        List<Challenge> selected = new ArrayList<>();
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.EASY, Collections.emptyList()), easyCount));
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.MEDIUM, Collections.emptyList()), mediumCount));
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.HARD, Collections.emptyList()), hardCount));

        if (selected.isEmpty()) {
            return Collections.emptyList();
        }

        List<DailyMemberChallenge> newChallenges = selected.stream()
                .map(challenge -> DailyMemberChallenge.builder()
                        .member(member)
                        .challenge(challenge)
                        .challengeDate(today)
                        .build())
                .toList();

        return dailyMemberChallengeRepository.saveAll(newChallenges);
    }

    private List<Challenge> selectRandomChallenges(List<Challenge> challenges, int count) {
        if (challenges.size() < count) {
            log.warn("Not enough challenges of a certain type to select. available: {}, requested: {}",
                    challenges.size(), count);
            return challenges;
        }
        Collections.shuffle(challenges);
        return challenges.subList(0, count);
    }

    private void expireOtherActiveChallenges(Member member, LocalDate date, Long completedChallengeId) {
        List<DailyMemberChallenge> activeChallenges = dailyMemberChallengeRepository
                .findByMemberAndChallengeDateAndChallengeStatus(member, date, ChallengeStatus.ACTIVE);
        for (DailyMemberChallenge challenge : activeChallenges) {
            if (!challenge.getId().equals(completedChallengeId)) {
                if (challenge.getChallenge().getChallengeType() != ChallengeType.HARD) {
                    challenge.updateChallengeStatus(ChallengeStatus.EXPIRED);
                }
            }
        }
    }

    private void updateStreak(Member member) {
        LocalDate today = LocalDate.now();
        LocalDate lastUpdate = member.getLastStreakUpdatedAt();

        if (lastUpdate == null) {
            member.resetStreakToOne();
        } else if (lastUpdate.isEqual(today.minusDays(1))) {
            member.incrementStreak();
        } else if (lastUpdate.isBefore(today.minusDays(1))) {
            member.resetStreakToOne();
        }

        member.updateLastStreakUpdatedAt(today);
    }


    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private DailyMemberChallenge findDailyChallengeById(Long id) {
        return dailyMemberChallengeRepository.findById(id).orElseThrow(DailyMemberChallengeNotFoundException::new);
    }
}
