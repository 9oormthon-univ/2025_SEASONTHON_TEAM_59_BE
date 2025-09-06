package com.leafup.leafupbackend.member.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyChallengeService {

    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final DailyMemberChallengeImageRepository dailyMemberChallengeImageRepository;

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Set<ChallengeStatus> EXCLUDED_STATUSES_FOR_ADVANCED = Set.of(
            ChallengeStatus.PENDING_APPROVAL,
            ChallengeStatus.COMPLETED,
            ChallengeStatus.REJECTED
    );

    @Transactional
    public DailyChallengesResDto getOrCreateTodaysChallenges(String email) {
        String cacheKey = "daily-challenges:" + email + ":" + LocalDate.now();

        String cachedData = redisTemplate.opsForValue().get(cacheKey);
        if (cachedData != null) {
            try {
                return objectMapper.readValue(cachedData, DailyChallengesResDto.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to parse cached data for key: {}", cacheKey, e);
            }
        }

        DailyChallengesResDto todaysChallengesDto = getChallengesFromDb(email);

        try {
            String jsonData = objectMapper.writeValueAsString(todaysChallengesDto);
            long ttlSeconds = Duration.between(LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()).getSeconds();
            redisTemplate.opsForValue().set(cacheKey, jsonData, Duration.ofSeconds(ttlSeconds));
        } catch (JsonProcessingException e) {
            log.error("Failed to cache data for key: {}", cacheKey, e);
        }

        return todaysChallengesDto;
    }

    private DailyChallengesResDto getChallengesFromDb(String email) {
        Member member = findMemberByEmail(email);
        LocalDate today = LocalDate.now();

        List<DailyMemberChallenge> todaysChallenges = dailyMemberChallengeRepository.findByMemberAndChallengeDate(member, today);
        if (todaysChallenges.isEmpty()) {
            updateStageBasedOnSubmissionOrder(member);
            todaysChallenges = createNewDailyChallenges(member, today);
        }

        List<DailyChallengeResDto> filteredChallenges = todaysChallenges.stream()
                .filter(dmc -> {
                    boolean isAdvanced = dmc.getChallenge().getChallengeType() == ChallengeType.HARD;
                    boolean isExcludedStatus = EXCLUDED_STATUSES_FOR_ADVANCED.contains(dmc.getChallengeStatus());

                    return !(isAdvanced && isExcludedStatus);
                })
                .map(dmc -> DailyChallengeResDto.of(dmc.getId(),
                        dmc.getChallenge().getContents(),
                        dmc.getChallenge().getChallengeType(),
                        dmc.getChallengeStatus()))
                .toList();

        int completedCount = dailyMemberChallengeRepository
                .countByMemberAndChallengeDateAndChallengeStatus(member, today, ChallengeStatus.COMPLETED);

        return DailyChallengesResDto.of(member.getCurrentStage(), completedCount, filteredChallenges);
    }

    @Transactional
    public void submitChallenge(String email, Long dailyMemberChallengeId, String imageUrl) {
        Member member = findMemberByEmail(email);
        DailyMemberChallenge dailyMemberChallenge = findDailyChallengeById(dailyMemberChallengeId);

        if (!dailyMemberChallenge.getMember().equals(member)) {
            throw new ChallengeOwnershipException();
        }

        dailyMemberChallenge.updateChallengeStatus(ChallengeStatus.PENDING_APPROVAL);
        DailyMemberChallengeImage dailyMemberChallengeImage = DailyMemberChallengeImage.builder()
                .dailyMemberChallenge(dailyMemberChallenge)
                .imageUrl(imageUrl)
                .build();

        dailyMemberChallengeImageRepository.save(dailyMemberChallengeImage);

        member.plusStage();
        updateStreak(member);
        replaceHardChallengeIfCompleted(dailyMemberChallenge);

        // 캐시 무효화
        deleteDailyChallengeCache(email, LocalDate.now());
    }

    public void deleteDailyChallengeCache(String email, LocalDate date) {
        String cacheKey = "daily-challenges:" + email + ":" + date;
        redisTemplate.delete(cacheKey);
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

    private void replaceHardChallengeIfCompleted(DailyMemberChallenge completedChallenge) {
        if (completedChallenge.getChallenge().getChallengeType() != ChallengeType.HARD) {
            return;
        }

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

    private void updateStageBasedOnSubmissionOrder(Member member) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<DailyMemberChallenge> yesterdaysDmcs = dailyMemberChallengeRepository.findByMemberAndChallengeDate(member,
                yesterday);
        if (yesterdaysDmcs.isEmpty()) {
            return;
        }

        List<DailyMemberChallengeImage> yesterdaysImages = dailyMemberChallengeImageRepository.findByDailyMemberChallengeIn(
                yesterdaysDmcs);
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
            memberRepository.save(member);
        }
    }

    private List<DailyMemberChallenge> createNewDailyChallenges(Member member, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        Set<Long> challengesToExclude = dailyMemberChallengeRepository.findChallengeIdsByMemberAndChallengeDate(member,
                yesterday);

        List<Challenge> availableChallengesSource = challengesToExclude.isEmpty() ?
                challengeRepository.findAll() :
                challengeRepository.findChallengesToSelect(challengesToExclude);

        Map<ChallengeType, List<Challenge>> availableChallenges = availableChallengesSource.stream()
                .filter(c -> !challengesToExclude.contains(c.getId()))
                .collect(Collectors.groupingBy(Challenge::getChallengeType));

        List<Challenge> selected = new ArrayList<>();
        selected.addAll(selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.EASY, Collections.emptyList()), 2));
        selected.addAll(
                selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.MEDIUM, Collections.emptyList()), 2));
        selected.addAll(selectRandomChallenges(availableChallenges.getOrDefault(ChallengeType.HARD, Collections.emptyList()), 1));

        List<DailyMemberChallenge> newChallenges = selected.stream()
                .map(challenge -> DailyMemberChallenge.builder()
                        .member(member)
                        .challenge(challenge)
                        .challengeDate(today)
                        .build())
                .collect(Collectors.toList());

        return dailyMemberChallengeRepository.saveAll(newChallenges);
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
