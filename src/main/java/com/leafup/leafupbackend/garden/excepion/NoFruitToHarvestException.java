package com.leafup.leafupbackend.garden.excepion;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class NoFruitToHarvestException extends InvalidGroupException {
    public NoFruitToHarvestException() {
        super("수확할 열매가 없습니다. 챌린지를 완료하여 열매를 맺어보세요.");
    }

}
