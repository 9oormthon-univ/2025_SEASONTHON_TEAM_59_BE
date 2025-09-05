package com.leafup.leafupbackend.point.domain;

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
public class PointHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int amount;

    private String description;

    @Builder
    private PointHistory(Member member, int amount, String description) {
        this.member = member;
        this.amount = amount;
        this.description = description;
    }

}
