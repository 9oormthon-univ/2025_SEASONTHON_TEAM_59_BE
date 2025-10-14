package com.leafup.leafupbackend.auth.application;

import com.leafup.leafupbackend.auth.api.dto.response.UserInfo;
import com.leafup.leafupbackend.auth.exception.ExistsMemberEmailException;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.application.LevelService;
import com.leafup.leafupbackend.member.domain.Avatar;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.MemberAvatar;
import com.leafup.leafupbackend.member.domain.SocialType;
import com.leafup.leafupbackend.member.domain.repository.AvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberAvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.AvatarNotFoundException;
import com.leafup.leafupbackend.member.exception.NoEquippedAvatarException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthMemberService {

    private final static String INTRODUCTION = "저와 함께 환경을 지켜보아요.";

    private final MemberRepository memberRepository;
    private final AvatarRepository avatarRepository;
    private final LevelService levelService;
    private final MemberAvatarRepository memberAvatarRepository;

    @Transactional
    public MemberInfoResDto saveUserInfo(UserInfo userInfo, SocialType provider) {
        Member member = memberRepository.findByEmail(userInfo.email())
                .map(existingMember -> {
                    validateSocialType(existingMember, provider);
                    return existingMember;
                })
                .orElseGet(() -> {
                    Member newMember = createMember(userInfo, provider);
                    assignDefaultAvatar(newMember);
                    return newMember;
                });

        String avatarUrl = memberAvatarRepository.findEquippedByMemberWithAvatar(member)
                .map(memberAvatar -> memberAvatar.getAvatar().getAvatarUrl())
                .orElseThrow(NoEquippedAvatarException::new);

        int expBarPercent = levelService.getExpBarPercent(member);

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
                avatarUrl,
                expBarPercent);
    }

    private Member createMember(UserInfo userInfo, SocialType provider) {
        String userPicture = getUserPicture(userInfo.picture());
        String name = userInfo.nickname() != null ? userInfo.nickname() : userInfo.name();

        return memberRepository.save(
                Member.builder()
                        .email(userInfo.email())
                        .name(name)
                        .picture(userPicture)
                        .socialType(provider)
                        .introduction(INTRODUCTION)
                        .build()
        );
    }

    private String getUserPicture(String picture) {
        if (picture == null) {
            return null;
        }

        return convertToHighRes(picture);
    }

    private String convertToHighRes(String url) {
        return url.replace("s96-c", "s2048-c");
    }

    private void assignDefaultAvatar(Member member) {
        Avatar defaultAvatar = avatarRepository.findById(1L).orElseThrow(AvatarNotFoundException::new);

        MemberAvatar memberAvatar = MemberAvatar.builder().member(member).avatar(defaultAvatar).build();
        memberAvatar.equip();
        memberAvatarRepository.save(memberAvatar);
    }

    private void validateSocialType(Member member, SocialType provider) {
        if (!provider.equals(member.getSocialType())) {
            throw new ExistsMemberEmailException();
        }
    }

}
