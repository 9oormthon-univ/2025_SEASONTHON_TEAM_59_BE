package com.leafup.leafupbackend.ranking.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import com.leafup.leafupbackend.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "monthly_ranking", indexes = @Index(name = "idx_monthly_ranking_year_month_region", columnList = "year, month, region, point DESC"))
public class MonthlyRanking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private int year;

    private int month;

    private String region;

    private int point;

    @Builder
    private MonthlyRanking(Member member, int year, int month, String region, int point) {
        this.member = member;
        this.year = year;
        this.month = month;
        this.region = region;
        this.point = point;
    }

}
