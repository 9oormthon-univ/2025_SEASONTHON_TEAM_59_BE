package com.leafup.leafupbackend.point.application.dto;

import com.leafup.leafupbackend.member.domain.Member;

public record PointAggregationDto(Member member, Long totalPoints) {
}