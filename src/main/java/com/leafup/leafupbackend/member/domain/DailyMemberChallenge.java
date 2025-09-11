package com.leafup.leafupbackend.member.domain;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyMemberChallenge extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    private LocalDate challengeDate;

    @Enumerated(EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @OneToMany(mappedBy = "dailyMemberChallenge", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DailyMemberChallengeImage> dailyMemberChallengeImages = new ArrayList<>();

    @Builder
    private DailyMemberChallenge(Member member, Challenge challenge, LocalDate challengeDate) {
        this.member = member;
        this.challenge = challenge;
        this.challengeDate = challengeDate;
        this.challengeStatus = ChallengeStatus.ACTIVE;
    }

    public void updateChallengeStatus(ChallengeStatus challengeStatus) {
        this.challengeStatus = challengeStatus;
    }

}
