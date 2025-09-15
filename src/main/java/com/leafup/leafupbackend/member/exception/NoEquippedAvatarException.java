package com.leafup.leafupbackend.member.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class NoEquippedAvatarException extends NotFoundGroupException {
    public NoEquippedAvatarException() {
        super("사용자에게 장착된 아바타가 없습니다. 데이터 무결성을 확인해주세요.");
    }
}