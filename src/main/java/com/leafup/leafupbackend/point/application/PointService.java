package com.leafup.leafupbackend.point.application;

import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.point.domain.PointHistory;
import com.leafup.leafupbackend.point.domain.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void addPoint(String email, int amount, String description) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        member.plusPoint(amount);

        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(amount)
                .description(description)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    @Transactional
    public void minusPoint(String email, int amount, String description) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        member.minusPoint(amount);

        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .amount(-amount)
                .description(description)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

}
