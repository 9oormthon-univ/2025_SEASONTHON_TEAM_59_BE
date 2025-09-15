package com.leafup.leafupbackend.member.application;

import com.leafup.leafupbackend.member.api.dto.response.OwnedAvatarResDto;
import com.leafup.leafupbackend.member.api.dto.response.OwnedAvatarsResDto;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.MemberAvatar;
import com.leafup.leafupbackend.member.domain.repository.MemberAvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.AvatarOwnershipException;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberAvatarService {

    private final MemberRepository memberRepository;
    private final MemberAvatarRepository memberAvatarRepository;

    public OwnedAvatarsResDto getOwnedAvatars(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<MemberAvatar> ownedAvatars = memberAvatarRepository.findByMember(member);

        return OwnedAvatarsResDto.from(ownedAvatars.stream()
                .map(ma -> OwnedAvatarResDto.from(
                        ma.getAvatar().getId(),
                        ma.getAvatar().getAvatarUrl(),
                        ma.isEquipped())
                )
                .toList());
    }

    @Transactional
    public void equipAvatar(String email, Long avatarIdToEquip) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        List<MemberAvatar> ownedAvatars = memberAvatarRepository.findByMember(member);

        ownedAvatars.stream()
                .filter(MemberAvatar::isEquipped)
                .forEach(MemberAvatar::unequip);

        MemberAvatar avatarToEquip = ownedAvatars.stream()
                .filter(ma -> ma.getAvatar().getId().equals(avatarIdToEquip))
                .findFirst()
                .orElseThrow(AvatarOwnershipException::new);

        avatarToEquip.equip();
    }

}
