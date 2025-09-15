package com.leafup.leafupbackend.store.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class InsufficientPointsException extends InvalidGroupException {
    public InsufficientPointsException(String message) {
        super(message);
    }

    public InsufficientPointsException() {
        this("포인트가 부족합니다.");
    }
}
