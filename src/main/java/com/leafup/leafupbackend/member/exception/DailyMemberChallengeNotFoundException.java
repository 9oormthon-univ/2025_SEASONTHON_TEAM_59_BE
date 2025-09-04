package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class DailyMemberChallengeNotFoundException extends NotFoundGroupException {
    public DailyMemberChallengeNotFoundException(String message) {
        super(message);
    }

    public DailyMemberChallengeNotFoundException() {
        this("존재하지 않는 일일 챌린지입니다.");
    }
}
