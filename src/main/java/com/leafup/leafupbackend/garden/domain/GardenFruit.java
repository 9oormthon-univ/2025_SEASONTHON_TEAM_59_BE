package com.leafup.leafupbackend.garden.domain;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GardenFruit extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_garden_id")
    private WeeklyGarden weeklyGarden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    private boolean harvested;

    @Builder
    public GardenFruit(WeeklyGarden weeklyGarden, Challenge challenge) {
        this.weeklyGarden = weeklyGarden;
        this.challenge = challenge;
        this.harvested = false;
    }

    public void harvest() {
        this.harvested = true;
    }

}