package com.leafup.leafupbackend.member.domain.repository;

import com.leafup.leafupbackend.member.domain.Avatar;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.MemberAvatar;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MemberAvatarRepository extends JpaRepository<MemberAvatar, Long> {

    List<MemberAvatar> findByMember(Member member);

    boolean existsByMemberAndAvatar(Member member, Avatar avatar);

    @Query("SELECT ma FROM MemberAvatar ma JOIN FETCH ma.avatar WHERE ma.member = :member")
    List<MemberAvatar> findByMemberWithAvatar(@Param("member") Member member);

    @Query("SELECT ma FROM MemberAvatar ma JOIN FETCH ma.avatar WHERE ma.member = :member AND ma.isEquipped = true")
    Optional<MemberAvatar> findEquippedByMemberWithAvatar(@Param("member") Member member);

}
