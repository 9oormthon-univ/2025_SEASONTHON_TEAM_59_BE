package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class MemberNotFoundException extends NotFoundGroupException {
    public MemberNotFoundException(String message) {
        super(message);
    }

    public MemberNotFoundException() {
        this("존재하지 않는 사용자 입니다.");
    }
}
