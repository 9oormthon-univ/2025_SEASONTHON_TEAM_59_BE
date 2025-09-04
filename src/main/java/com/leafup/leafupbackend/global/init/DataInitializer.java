package com.leafup.leafupbackend.global.init;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import com.leafup.leafupbackend.challenge.domain.repository.ChallengeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ChallengeRepository challengeRepository;

    @Override
    @Transactional
    public void run(String... args) {
        List<Challenge> challenges = List.of(
                // 초급
                createChallenge("텀블러 사용하기", ChallengeType.EASY),
                createChallenge("장보기 - 장바구니·에코백 사용하기", ChallengeType.EASY),
                createChallenge("찬물 세탁하기", ChallengeType.EASY),
                createChallenge("잔반 남기지 않기", ChallengeType.EASY),
                createChallenge("버스·지하철 타기", ChallengeType.EASY),
                createChallenge("(1km이상)목적지까지 걸어가기", ChallengeType.EASY),
                createChallenge("(펜으로 날짜 기재)이면지 사용하기", ChallengeType.EASY),
                createChallenge("자전거 이용하기", ChallengeType.EASY),
                createChallenge("깨끗한 자연을 소중히-하늘 촬영하기", ChallengeType.EASY),
                createChallenge("메일함 비우기", ChallengeType.EASY),

                // 중급
                createChallenge("(야외샷)올바른 분리배출", ChallengeType.MEDIUM),
                createChallenge("실내 식물 키우기", ChallengeType.MEDIUM),
                createChallenge("도시락 싸가기", ChallengeType.MEDIUM),
                createChallenge("실내 26도 이상 유지하기", ChallengeType.MEDIUM),
                createChallenge("물을 받아서 설거지하기", ChallengeType.MEDIUM),
                createChallenge("다회용기 사용 배달음식 주문하기", ChallengeType.MEDIUM),
                createChallenge("비닐 없이 포장된 친환경 제품 구매", ChallengeType.MEDIUM),
                createChallenge("배달용기 설거지하기", ChallengeType.MEDIUM),

                // 고급
                createChallenge("플로깅(쓰레기 줍기 활동)", ChallengeType.HARD),
                createChallenge("컴퓨터 절전 프로그램 ‘그린터치’ 설치하기 (1회성으로 스테이지 출현)", ChallengeType.HARD),
                createChallenge("환경 자원봉사 하기", ChallengeType.HARD),
                createChallenge("빗물받이 주변 치우기", ChallengeType.HARD)
        );

        challengeRepository.saveAll(challenges);
    }

    private Challenge createChallenge(String contents, ChallengeType challengeType) {
        return Challenge.builder()
                .contents(contents)
                .challengeType(challengeType)
                .build();
    }

}
