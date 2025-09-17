package com.leafup.leafupbackend.garden.excepion;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class AlreadyHarvestedTodayException extends InvalidGroupException {

    public AlreadyHarvestedTodayException() {
        super("오늘 이미 텃밭에서 수확했습니다. 내일 다시 시도해주세요.");
    }

}
