package com.leafup.leafupbackend.image.exception;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class ImageAlreadyUploadedException extends InvalidGroupException {
    public ImageAlreadyUploadedException(String message) {
        super(message);
    }

    public ImageAlreadyUploadedException() {
        this("이미 이미지를 업로드했습니다.");
    }
}
