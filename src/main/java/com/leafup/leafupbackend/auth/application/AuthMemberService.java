package com.leafup.leafupbackend.auth.application;

import com.leafup.leafupbackend.auth.api.dto.response.UserInfo;
import com.leafup.leafupbackend.auth.exception.ExistsMemberEmailException;
import com.leafup.leafupbackend.member.api.dto.response.MemberInfoResDto;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.SocialType;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthMemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberInfoResDto saveUserInfo(UserInfo userInfo, SocialType provider) {
        Member member = getExistingMemberOrCreateNew(userInfo, provider);

        validateSocialType(member, provider);

        return MemberInfoResDto.of(member.getEmail(),
                member.getPicture(),
                String.valueOf(member.getSocialType()),
                member.isFirstLogin(),
                member.getNickname(),
                member.getCode(),
                member.isLocationAgreed(),
                member.isCameraAccessAllowed());
    }

    private Member getExistingMemberOrCreateNew(UserInfo userInfo, SocialType provider) {
        return memberRepository.findByEmail(userInfo.email()).orElseGet(() -> createMember(userInfo, provider));
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
                        .build()
        );
    }

    private String getUserPicture(String picture) {
        return Optional.ofNullable(picture)
                .map(this::convertToHighRes)
                .orElseThrow(null);
    }

    private String convertToHighRes(String url) {
        return url.replace("s96-c", "s2048-c");
    }

    private void validateSocialType(Member member, SocialType provider) {
        if (!provider.equals(member.getSocialType())) {
            throw new ExistsMemberEmailException();
        }
    }

}
