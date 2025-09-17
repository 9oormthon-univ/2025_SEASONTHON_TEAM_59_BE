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
public class Avatar extends BaseEntity {

    private String name;

    @Enumerated(value = EnumType.STRING)
    private AvatarType type;

    private String avatarUrl;

    private int point;

    @Builder
    private Avatar(String name, AvatarType type, String avatarUrl, int point) {
        this.name = name;
        this.type = type;
        this.avatarUrl = avatarUrl;
        this.point = point;
    }

}
