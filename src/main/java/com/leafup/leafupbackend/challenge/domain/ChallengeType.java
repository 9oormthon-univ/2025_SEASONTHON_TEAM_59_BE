package com.leafup.leafupbackend.challenge.domain;

import lombok.Getter;

@Getter
public enum ChallengeType {

    EASY("쉬움", 6),
    MEDIUM("중간", 15),
    HARD("어려움", 50);

    private final String description;
    private final int point;

    ChallengeType(String description, int point) {
        this.description = description;
        this.point = point;
    }

}
