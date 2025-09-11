package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.ConflictGroupException;

public class BonusAlreadyClaimedException extends ConflictGroupException {
    public BonusAlreadyClaimedException(String message) {
        super(message);
    }

    public BonusAlreadyClaimedException() {
        this("이미 오늘 일일 챌린지 완료 보너스를 수령했습니다.");
    }
}
