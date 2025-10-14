package com.leafup.leafupbackend.friend.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class FriendshipNotFoundException extends NotFoundGroupException {
    public FriendshipNotFoundException(String message) {
        super(message);
    }

    public FriendshipNotFoundException() {
        this("해당 친구 요청을 찾을 수 없습니다.");
    }
}
