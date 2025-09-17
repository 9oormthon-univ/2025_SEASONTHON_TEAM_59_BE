package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.point.application.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LevelService {

    private final PointService pointService;

    /**
     * 사용자에게 경험치와 재화(포인트)를 부여하고, 레벨업을 처리합니다.
     *
     * @param member      대상 사용자
     * @param amount      부여할 경험치와 포인트의 양
     * @param description 획득 사유 (포인트 이력용)
     */
    @Transactional
    public void grantExpAndPoint(Member member, int amount, String description) {
        // 1. 재화(포인트) 부여 및 이력 기록
        pointService.addPoint(member.getEmail(), amount, description);
        member.plusPoint(amount);

        // 2. 경험치 부여
        member.plusExp(amount);

        // 3. 레벨업 처리
        handleLevelUp(member);
    }

    /**
     * 사용자의 현재 경험치를 기반으로 레벨업을 처리합니다. 요구 경험치를 초과하면 여러 번 레벨업 할 수 있습니다.
     */
    private void handleLevelUp(Member member) {
        // 다음 레벨에 필요한 총 누적 경험치
        int nextLevelTotalExp = getTotalExpForLevel(member.getLevel() + 1);

        // 현재 경험치가 다음 레벨업 요구량을 충족하는 동안 반복
        while (member.getExp() >= nextLevelTotalExp) {
            member.levelUp();
            log.info("Member {} leveled up to level {}!", member.getId(), member.getLevel());

            // 다음 레벨업을 위한 요구 경험치 재계산
            nextLevelTotalExp = getTotalExpForLevel(member.getLevel() + 1);
        }
    }

    /**
     * 현재 레벨의 경험치 바 진행률(%)을 계산합니다. 이 메서드는 주로 사용자 정보를 보여주는 DTO를 만들 때 사용됩니다.
     */
    public int getExpBarPercent(Member member) {
        int level = member.getLevel();
        int currentTotalExp = member.getExp();
        int baseExpForCurrentLevel = getTotalExpForLevel(level);
        int baseExpForNextLevel = getTotalExpForLevel(level + 1);

        int expGainedInCurrentLevel = currentTotalExp - baseExpForCurrentLevel;
        int expRequiredForNextLevel = baseExpForNextLevel - baseExpForCurrentLevel;

        if (expRequiredForNextLevel <= 0) {
            return 100; // 데이터 오류 또는 최고 레벨 도달 시
        }

        return (int) Math.min(100, Math.round((expGainedInCurrentLevel * 100.0) / expRequiredForNextLevel));
    }

    /**
     * 특정 레벨에서 다음 레벨로 올라가기 위해 필요한 경험치를 계산합니다.
     *
     * @param currentLevel 현재 레벨
     * @return 필요 경험치
     */
    public int getExpToNextLevel(int currentLevel) {
        if (currentLevel < 1) {
            return 0;
        }
        if (currentLevel == 1) {
            return 10;
        }
        return (int) Math.round(10 + 1.6 * (currentLevel - 1));
    }

    /**
     * 특정 레벨에 도달하기 위해 필요한 총 누적 경험치를 계산합니다.
     *
     * @param level 목표 레벨
     * @return 총 누적 경험치
     */
    public int getTotalExpForLevel(int level) {
        if (level <= 1) {
            return 0;
        }

        int totalExp = 0;
        for (int i = 1; i < level; i++) {
            totalExp += getExpToNextLevel(i);
        }
        return totalExp;
    }
}
