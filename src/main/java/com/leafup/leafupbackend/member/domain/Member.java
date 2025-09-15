package com.leafup.leafupbackend.member.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    private String name;

    private String picture;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    private boolean isFirstLogin;

    private String nickname;

    private String code;

    private boolean isLocationAgreed;

    private boolean isCameraAccessAllowed;

    private String address;

    private int level;

    private int exp;

    private int point;

    private int currentStage;

    private int streak;

    private LocalDate lastStreakUpdatedAt;

    private LocalDate lastDailyBonusClaimedAt;

    @Builder
    private Member(String email, String name, String picture, SocialType socialType) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.socialType = socialType;
        this.role = Role.USER;
        this.isFirstLogin = true;
        this.level = 1;
        this.exp = 0;
        this.point = 0;
        this.currentStage = 1;
        this.streak = 0;
        this.lastStreakUpdatedAt = null;
        this.lastDailyBonusClaimedAt = null;
    }

    public void updateFirstLogin() {
        this.isFirstLogin = false;
    }

    public void onboarding(String nickname, String code, boolean isLocationAgreed,
                           boolean isCameraAccessAllowed, String address) {
        this.nickname = nickname;
        this.code = code;
        this.isLocationAgreed = isLocationAgreed;
        this.isCameraAccessAllowed = isCameraAccessAllowed;
        this.address = address;
    }

    public void updatePicture(String picture) {
        this.picture = picture;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void plusStage() {
        this.currentStage += 1;
    }

    public void updateCurrentStage(int currentStage) {
        this.currentStage = currentStage;
    }

    public void updateLevel(int level) {
        this.level = level;
    }

    public void updateExp(int exp) {
        this.exp = exp;
    }

    public void plusPoint(int point) {
        this.point += point;
    }

    public void minusPoint(int point) {
        if (this.point < point) {
            // 불가능.
            return;
        }

        this.point -= point;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public void resetStreakToOne() {
        this.streak = 1;
    }

    public void updateLastStreakUpdatedAt(LocalDate date) {
        this.lastStreakUpdatedAt = date;
    }

    public void updateLastDailyBonusClaimedAt(LocalDate date) {
        this.lastDailyBonusClaimedAt = date;
    }

}
