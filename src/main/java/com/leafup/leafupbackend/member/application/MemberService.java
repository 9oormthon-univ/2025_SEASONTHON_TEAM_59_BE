package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.member.api.dto.request.OnboardingReqDto;
import com.leafup.leafupbackend.member.api.dto.request.UpdateNicknameReqDto;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberAvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.BonusAlreadyClaimedException;
import com.leafup.leafupbackend.member.exception.DailyBonusNotEligibleException;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.member.exception.NoEquippedAvatarException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final MemberAvatarRepository memberAvatarRepository;
    private final LevelService levelService;

    public MemberInfoResDto getInfo(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        return getMemberInfoResDto(member);
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

        return getMemberInfoResDto(member);
    }

    @Transactional
    public void updateNickname(String email, UpdateNicknameReqDto updateNicknameReqDto) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        if (member.getNickname().equals(updateNicknameReqDto.nickname())) {
            return;
        }

        member.updateNickname(updateNicknameReqDto.nickname());
    }

    @Transactional
    public void claimDailyCompletionBonus(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        LocalDate today = LocalDate.now();

        if (today.equals(member.getLastDailyBonusClaimedAt())) {
            throw new BonusAlreadyClaimedException();
        }

        int completedCount = dailyMemberChallengeRepository
                .countByMemberAndChallengeDateAndChallengeStatus(member, today, ChallengeStatus.COMPLETED);

        if (completedCount >= 3) {
            levelService.addPointAndHandleLevelUpAndExp(member, 20, "일일 챌린지 3개 완료 보너스");
            member.updateLastDailyBonusClaimedAt(today);
            member.incrementDailyCompletionCount();
        } else {
            throw new DailyBonusNotEligibleException();
        }
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

    private MemberInfoResDto getMemberInfoResDto(Member member) {
        String avatarUrl = memberAvatarRepository.findEquippedByMemberWithAvatar(member)
                .map(memberAvatar -> memberAvatar.getAvatar().getAvatarUrl())
                .orElseThrow(NoEquippedAvatarException::new);

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
                member.getPoint(),
                avatarUrl);
    }

}
