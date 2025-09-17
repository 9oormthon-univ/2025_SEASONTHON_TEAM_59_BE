package com.leafup.leafupbackend.achievement.exception;

import com.github.giwoong01.springapicommon.error.exception.ConflictGroupException;

public class AchievementAlreadyClaimedException extends ConflictGroupException {
    public AchievementAlreadyClaimedException() {
        super("이미 획득한 업적입니다.");
    }
}