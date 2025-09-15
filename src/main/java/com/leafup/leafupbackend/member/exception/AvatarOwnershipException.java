package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.AccessDeniedGroupException;

public class AvatarOwnershipException extends AccessDeniedGroupException {
    public AvatarOwnershipException(String message) {
        super(message);
    }

    public AvatarOwnershipException() {
        this("본인의 아바타가 아닙니다.");
    }
}
