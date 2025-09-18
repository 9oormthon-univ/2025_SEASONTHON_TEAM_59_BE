package com.leafup.leafupbackend.garden.excepion;

import com.github.giwoong01.springapicommon.error.exception.InvalidGroupException;

public class FruitAlreadyHarvestedException extends InvalidGroupException {
    public FruitAlreadyHarvestedException() {
        super("이미 수확한 열매입니다.");
    }
}