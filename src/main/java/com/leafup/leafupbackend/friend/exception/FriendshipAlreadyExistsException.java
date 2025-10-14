package com.leafup.leafupbackend.friend.exception;

import com.github.giwoong01.springapicommon.error.exception.ConflictGroupException;

public class FriendshipAlreadyExistsException extends ConflictGroupException {
    public FriendshipAlreadyExistsException(String message) {
        super(message);
    }

    public FriendshipAlreadyExistsException() {
        this("이미 친구 요청을 보냈거나 친구 관계입니다.");
    }
}
