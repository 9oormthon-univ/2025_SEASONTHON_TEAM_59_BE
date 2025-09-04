package com.leafup.leafupbackend.member.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyMemberChallengeImage extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_member_challenge_id")
    private DailyMemberChallenge dailyMemberChallenge;

    private String imageUrl;

    @Builder
    private DailyMemberChallengeImage(DailyMemberChallenge dailyMemberChallenge, String imageUrl) {
        this.dailyMemberChallenge = dailyMemberChallenge;
        this.imageUrl = imageUrl;
    }

}
