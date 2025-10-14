package com.leafup.leafupbackend.friend.domain;

import com.leafup.leafupbackend.global.entity.BaseEntity;
import com.leafup.leafupbackend.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friendship",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friendship_requester_receiver",
                        columnNames = {"requester_id", "receiver_id"}
                )
        })
public class Friendship extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

    @Builder
    private Friendship(Member requester, Member receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = FriendshipStatus.PENDING;
    }

    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }

    public void reject() {
        this.status = FriendshipStatus.REJECTED;
    }

}
