package com.leafup.leafupbackend.image.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class ImageNotFoundException extends NotFoundGroupException {
    public ImageNotFoundException(String message) {
        super(message);
    }

    public ImageNotFoundException() {
        this("존재하지 않는 이미지 입니다.");
    }
}
