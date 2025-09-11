package com.leafup.leafupbackend.challenge.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Challenge extends BaseEntity {

    private String contents;

    @Enumerated(value = EnumType.STRING)
    private ChallengeType challengeType;

    private boolean isTwoCut;

    @Builder
    private Challenge(String contents, ChallengeType challengeType) {
        this.contents = contents;
        this.challengeType = challengeType;
        this.isTwoCut = false;
    }

}
