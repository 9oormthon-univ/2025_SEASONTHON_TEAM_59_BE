package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class ExistsNickNameException extends InvalidGroupException {
    public ExistsNickNameException(String message) {
        super(message);
    }

    public ExistsNickNameException() {
        this("이미 존재하는 닉네임입니다.");
    }
}
