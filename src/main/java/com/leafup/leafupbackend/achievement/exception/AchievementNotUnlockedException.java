package com.leafup.leafupbackend.achievement.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class AchievementNotUnlockedException extends InvalidGroupException {
    public AchievementNotUnlockedException() {
        super("업적 달성 조건을 만족하지 못했습니다.");
    }
}