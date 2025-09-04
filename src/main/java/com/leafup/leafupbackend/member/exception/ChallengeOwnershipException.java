package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.AccessDeniedGroupException;

public class ChallengeOwnershipException extends AccessDeniedGroupException {
    public ChallengeOwnershipException(String message) {
        super(message);
    }

    public ChallengeOwnershipException() {
        this("본인의 일일 챌린지가 아닙니다.");
    }
}
