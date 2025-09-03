package com.leafup.leafupbackend.image.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import com.leafup.leafupbackend.member.domain.Member;
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
public class Image extends BaseEntity {

    private String convertImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Image(String convertImageUrl, Member member) {
        this.convertImageUrl = convertImageUrl;
        this.member = member;
    }

}
