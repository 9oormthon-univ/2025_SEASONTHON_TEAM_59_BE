package com.leafup.leafupbackend.achievement.application;

import com.leafup.leafupbackend.achievement.api.dto.response.AchievementStatusResDto;
import com.leafup.leafupbackend.achievement.api.dto.response.ClaimedAchievementResDto;
import com.leafup.leafupbackend.achievement.domain.Achievement;
import com.leafup.leafupbackend.achievement.domain.AchievementType;
import com.leafup.leafupbackend.achievement.domain.MemberAchievement;
import com.leafup.leafupbackend.achievement.domain.repository.AchievementRepository;
import com.leafup.leafupbackend.achievement.domain.repository.MemberAchievementRepository;
import com.leafup.leafupbackend.achievement.exception.AchievementAlreadyClaimedException;
import com.leafup.leafupbackend.achievement.exception.AchievementNotUnlockedException;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import java.util.HashMap;
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
public class AchievementService {

    private final MemberRepository memberRepository;
    private final AchievementRepository achievementRepository;
    private final MemberAchievementRepository memberAchievementRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;

    public List<AchievementStatusResDto> getAchievementStatus(String email) {
        Member member = memberRepository.findByEmailWithAchievements(email)
                .orElseThrow(MemberNotFoundException::new);

        List<Achievement> allAchievements = achievementRepository.findAll();
        Set<Long> claimedAchievementIds = member.getMemberAchievements().stream()
                .map(ma -> ma.getAchievement().getId())
                .collect(Collectors.toSet());

        return allAchievements.stream()
                .map(achievement -> {
                    String status = determineStatus(member, achievement, claimedAchievementIds);
                    return AchievementStatusResDto.of(achievement.getId(),
                            achievement.getName(),
                            achievement.getDescription(),
                            status);
                })
                .toList();
    }

    public List<ClaimedAchievementResDto> getClaimedAchievements(String email) {
        Member member = memberRepository.findByEmailWithAchievements(email)
                .orElseThrow(MemberNotFoundException::new);

        List<MemberAchievement> claimedAchievements = member.getMemberAchievements();

        Map<AchievementType, MemberAchievement> latestAchievements = new HashMap<>();
        for (MemberAchievement memberAchievement : claimedAchievements) {
            Achievement achievement = memberAchievement.getAchievement();
            AchievementType type = achievement.getType();
            int level = achievement.getLevel();

            if (!latestAchievements.containsKey(type) || level > latestAchievements.get(type).getAchievement().getLevel()) {
                latestAchievements.put(type, memberAchievement);
            }
        }

        return latestAchievements.values().stream()
                .map(MemberAchievement::getAchievement)
                .map(achievement -> ClaimedAchievementResDto.of(
                                achievement.getId(),
                                achievement.getName(),
                                achievement.getDescription()
                        )
                )
                .toList();
    }

    @Transactional
    public void claimAchievement(String email, Long achievementId) {
        Member member = memberRepository.findByEmailWithAchievements(email)
                .orElseThrow(MemberNotFoundException::new);
        Achievement achievementToClaim = achievementRepository.findById(achievementId)
                .orElseThrow(() -> new EntityNotFoundException("Achievement not found"));

        Set<Long> claimedAchievementIds = member.getMemberAchievements().stream()
                .map(ma -> ma.getAchievement().getId())
                .collect(Collectors.toSet());

        String status = determineStatus(member, achievementToClaim, claimedAchievementIds);

        if ("LOCKED".equals(status)) {
            throw new AchievementNotUnlockedException();
        }
        if ("CLAIMED".equals(status)) {
            throw new AchievementAlreadyClaimedException();
        }

        MemberAchievement newMemberAchievement = MemberAchievement.builder()
                .member(member)
                .achievement(achievementToClaim)
                .build();
        memberAchievementRepository.save(newMemberAchievement);
    }

    private String determineStatus(Member member, Achievement achievement, Set<Long> claimedAchievementIds) {
        if (claimedAchievementIds.contains(achievement.getId())) {
            return "CLAIMED";
        }

        boolean isUnlocked = switch (achievement.getType()) {
            case FIRST_CHALLENGE ->
                    dailyMemberChallengeRepository.countByMemberAndChallengeStatus(member, ChallengeStatus.COMPLETED)
                            >= achievement.getRequiredCount();
            case STAGE_PARTICIPATION -> (member.getCurrentStage() - 1) >= achievement.getRequiredCount();
            case DAILY_HARVEST -> member.getDailyCompletionCount() >= achievement.getRequiredCount();
            case GARDENER -> member.getWeeklyGardenCompletionCount() >= achievement.getRequiredCount();
            case LEAF_COLLECTOR -> member.getStorePurchaseCount() >= achievement.getRequiredCount();
        };

        return isUnlocked ? "UNLOCKED" : "LOCKED";
    }
}