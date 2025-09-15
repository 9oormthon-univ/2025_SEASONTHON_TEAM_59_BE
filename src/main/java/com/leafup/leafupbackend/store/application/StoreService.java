package com.leafup.leafupbackend.store.application;

import com.leafup.leafupbackend.member.domain.Avatar;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.MemberAvatar;
import com.leafup.leafupbackend.member.domain.repository.AvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberAvatarRepository;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.AvatarNotFoundException;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import com.leafup.leafupbackend.store.api.dto.response.StoreAvatarResDto;
import com.leafup.leafupbackend.store.api.dto.response.StoreAvatarsResDto;
import com.leafup.leafupbackend.store.exception.AvatarAlreadyOwnedException;
import com.leafup.leafupbackend.store.exception.InsufficientPointsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final MemberRepository memberRepository;
    private final AvatarRepository avatarRepository;
    private final MemberAvatarRepository memberAvatarRepository;

    public StoreAvatarsResDto getAvatarsForSale(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        List<Avatar> allStoreAvatars = avatarRepository.findAll();
        List<MemberAvatar> ownedMemberAvatars = memberAvatarRepository.findByMemberWithAvatar(member);

        Map<Long, Boolean> ownedAvatarStatusMap = ownedMemberAvatars.stream()
                .collect(Collectors.toMap(
                        ma -> ma.getAvatar().getId(),
                        MemberAvatar::isEquipped
                ));

        return StoreAvatarsResDto.from(allStoreAvatars.stream()
                .map(avatar -> {
                    boolean isOwned = ownedAvatarStatusMap.containsKey(avatar.getId());
                    boolean isEquipped = isOwned && ownedAvatarStatusMap.get(avatar.getId());

                    return StoreAvatarResDto.builder()
                            .avatarId(avatar.getId())
                            .avatarUrl(avatar.getAvatarUrl())
                            .point(avatar.getPoint())
                            .isOwned(isOwned)
                            .isEquipped(isEquipped)
                            .build();
                })
                .toList());
    }

    @Transactional
    public void purchaseAvatar(String email, Long avatarId) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        Avatar avatar = avatarRepository.findById(avatarId).orElseThrow(AvatarNotFoundException::new);

        if (memberAvatarRepository.existsByMemberAndAvatar(member, avatar)) {
            throw new AvatarAlreadyOwnedException();
        }

        if (member.getPoint() < avatar.getPoint()) {
            throw new InsufficientPointsException();
        }

        member.minusPoint(avatar.getPoint());
        MemberAvatar memberAvatar = MemberAvatar.builder()
                .member(member)
                .avatar(avatar)
                .build();
        memberAvatarRepository.save(memberAvatar);
    }

}
