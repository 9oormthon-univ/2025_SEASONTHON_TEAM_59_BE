package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.point.application.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LevelService {

    private final PointService pointService;

    // 포인트 추가 및 레벨업 처리
    public void addPointAndHandleLevelUpAndExp(Member member, int point, String description) {
        pointService.addPoint(member.getEmail(), point, description);

        while (member.getPoint() >= getTotalXpForLevel(member.getLevel() + 1)) {
            member.updateLevel(member.getLevel() + 1);
        }

        member.updateExp(getExpBarPercent(member));
    }

    // 포인트 및 레벨 차감
    public void subtractPointAndHandleLevelAndExpDown(Member member, int point, String description) {
        pointService.minusPoint(member.getEmail(), point, description);

        while (member.getLevel() > 1 && member.getPoint() < getTotalXpForLevel(member.getLevel())) {
            member.updateLevel(member.getLevel() - 1);
        }

        member.updateExp(getExpBarPercent(member));
    }

    // 경험치 바 퍼센트 계산
    private int getExpBarPercent(Member member) {
        int level = member.getLevel();
        int currentXp = member.getPoint();
        int startXp = getTotalXpForLevel(level);
        int nextLevelXp = getTotalXpForLevel(level + 1);

        int gainedXp = currentXp - startXp;
        int requiredXp = nextLevelXp - startXp;

        if (requiredXp <= 0) {
            return 100;
        }

        return (int) Math.min(100, Math.round((gainedXp * 100.0) / requiredXp));
    }

    // 현재 레벨에서 다음 레벨까지 필요한 XP 반환
    private int getXpToNextLevel(int currentLevel) {
        if (currentLevel == 1) {
            return 10;
        }

        return (int) Math.round(10 + 1.6 * (currentLevel - 1));
    }

    // 누적 XP: 해당 레벨에 도달하기까지 필요한 총 XP
    private int getTotalXpForLevel(int level) {
        int xp = 0;
        for (int i = 1; i < level; i++) {
            xp += getXpToNextLevel(i);
        }

        return xp;
    }

}
