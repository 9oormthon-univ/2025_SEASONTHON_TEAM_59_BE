package com.leafup.leafupbackend.store.exception;

import com.github.giwoong01.springapicommon.error.exception.ConflictGroupException;

public class AvatarAlreadyOwnedException extends ConflictGroupException {
    public AvatarAlreadyOwnedException(String message) {
        super(message);
    }

    public AvatarAlreadyOwnedException() {
        this("이미 보유하고 있는 아바타입니다.");
    }
}
