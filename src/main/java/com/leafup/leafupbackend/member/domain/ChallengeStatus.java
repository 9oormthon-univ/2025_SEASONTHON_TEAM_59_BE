package com.leafup.leafupbackend.member.domain;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    ACTIVE("도전가능"),
    PENDING_APPROVAL("승인대기"),
    COMPLETED("완료"),
    REJECTED("(관리자)반려"),
    EXPIRED("만료됨");

    private final String description;

    ChallengeStatus(String description) {
        this.description = description;
    }

}
