package com.leafup.leafupbackend.challenge.application;

import com.leafup.leafupbackend.challenge.api.dto.response.ChallengeInfoResDto;
import com.leafup.leafupbackend.challenge.api.dto.response.ChallengesResDto;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import com.leafup.leafupbackend.challenge.domain.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengesResDto findChallengesByChallengeType(ChallengeType challengeType) {
        return ChallengesResDto.from(challengeRepository.findByChallengeType(challengeType).stream()
                .map(c -> {
                    return ChallengeInfoResDto.of(c.getContents(), c.getChallengeType().getPoint(), c.getChallengeType(), c.isTwoCut());
                })
                .toList());
    }
}
