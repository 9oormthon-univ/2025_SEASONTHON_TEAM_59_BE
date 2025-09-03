package com.leafup.leafupbackend.member.domain;

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
public class Member extends BaseEntity {

    private String email;

    private String name;

    private String picture;

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    private boolean isFirstLogin;

    private String nickname;

    private String code;

    private boolean isLocationAgreed;

    private boolean isCameraAccessAllowed;

    private String address;

    @Builder
    private Member(String email, String name, String picture, SocialType socialType) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.socialType = socialType;
        this.isFirstLogin = true;
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

}
