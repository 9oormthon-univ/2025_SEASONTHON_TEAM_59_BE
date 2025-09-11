package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class DailyBonusNotEligibleException extends InvalidGroupException {
    public DailyBonusNotEligibleException(String message) {
        super(message);
    }

    public DailyBonusNotEligibleException() {
        super("일일 챌린지 3개를 먼저 완료해야 보너스를 받을 수 있습니다.");
    }
}