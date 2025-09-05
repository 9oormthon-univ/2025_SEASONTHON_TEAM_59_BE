package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResDto getInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        return MemberInfoResDto.of(member.getEmail(),
                member.getPicture(),
                String.valueOf(member.getSocialType()),
                member.isFirstLogin(),
                member.getNickname(),
                member.getCode(),
                member.isLocationAgreed(),
                member.isCameraAccessAllowed(),
                member.getLevel(),
                member.getExp(),
                member.getPoint());
    }

    @Transactional
    public MemberInfoResDto onboarding(String email, OnboardingReqDto onboardingReqDto) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String code;
        do {
            code = randomAlphaNumeric(4);
        } while (memberRepository.existsByNicknameAndCode(onboardingReqDto.nickname(), code));

        member.onboarding(onboardingReqDto.nickname(),
                code,
                onboardingReqDto.locationAgreed(),
                onboardingReqDto.cameraAccessAllowed(),
                onboardingReqDto.address());

        member.updateFirstLogin();

        return MemberInfoResDto.of(member.getEmail(),
                member.getPicture(),
                String.valueOf(member.getSocialType()),
                member.isFirstLogin(),
                member.getNickname(),
                member.getCode(),
                member.isLocationAgreed(),
                member.isCameraAccessAllowed(),
                member.getLevel(),
                member.getExp(),
                member.getPoint());
    }

    private String randomAlphaNumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
