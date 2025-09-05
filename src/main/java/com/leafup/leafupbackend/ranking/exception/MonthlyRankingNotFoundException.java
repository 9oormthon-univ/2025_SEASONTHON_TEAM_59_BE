package com.leafup.leafupbackend.ranking.exception;

import com.github.giwoong01.springapicommon.error.exception.NotFoundGroupException;

public class MonthlyRankingNotFoundException extends NotFoundGroupException {
    public MonthlyRankingNotFoundException(String message) {
        super(message);
    }

    public MonthlyRankingNotFoundException() {
        super("월간 랭킹을 찾을 수 없습니다.");
    }
}
