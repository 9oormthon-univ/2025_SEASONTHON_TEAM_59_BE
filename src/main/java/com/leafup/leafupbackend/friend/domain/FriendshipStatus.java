package com.leafup.leafupbackend.friend.domain;

public enum FriendshipStatus {
    PENDING("친구 요청 상태"),
    ACCEPTED("친구 수락 상태"),
    REJECTED("친구 거절 상태");

    private final String description;

    FriendshipStatus(String description) {
        this.description = description;
    }
    
}
