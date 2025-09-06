package com.leafup.leafupbackend.garden.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import com.leafup.leafupbackend.member.domain.Member;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeeklyGarden extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int year;

    private int weekOfYear;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "weekly_garden_completed_challenges", joinColumns = @JoinColumn(name = "weekly_garden_id"))
    private Set<Long> completedChallengeIds = new HashSet<>();

    private boolean bonusAwarded;

    @Builder
    public WeeklyGarden(Member member, int year, int weekOfYear) {
        this.member = member;
        this.year = year;
        this.weekOfYear = weekOfYear;
        this.bonusAwarded = false;
    }

    public boolean addCompletedChallenge(Long challengeId) {
        return this.completedChallengeIds.add(challengeId);
    }

    public void markBonusAsAwarded() {
        this.bonusAwarded = true;
    }

}
