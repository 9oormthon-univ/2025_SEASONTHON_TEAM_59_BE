package com.leafup.leafupbackend.friend.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class FriendNotFoundException extends NotFoundGroupException {
    public FriendNotFoundException(String message) {
        super(message);
    }

    public FriendNotFoundException() {
        this("친구 목록에 해당 사용자가 없습니다.");
    }
}
