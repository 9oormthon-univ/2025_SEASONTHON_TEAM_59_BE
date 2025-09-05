package com.leafup.leafupbackend.point.application.dto;

import com.leafup.leafupbackend.member.domain.Member;

public record MonthlyPointAggregationDto(
        Member member,
        long totalPoints
) {
}
