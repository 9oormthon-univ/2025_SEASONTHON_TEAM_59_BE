package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class AvatarNotFoundException extends NotFoundGroupException {
    public AvatarNotFoundException(String message) {
        super(message);
    }

    public AvatarNotFoundException() {
        this("존재하지 않는 아바타 입니다.");
    }
}
