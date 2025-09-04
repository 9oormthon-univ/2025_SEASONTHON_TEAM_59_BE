package com.leafup.leafupbackend.challenge.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.challenge.api.docs.ChallengeControllerDocs;
import com.leafup.leafupbackend.challenge.api.dto.response.ChallengesResDto;
import com.leafup.leafupbackend.challenge.application.ChallengeService;
import com.leafup.leafupbackend.challenge.domain.ChallengeType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenges")
public class ChallengeController implements ChallengeControllerDocs {

    private final ChallengeService challengeService;

    @GetMapping
    public ResponseEntity<RspTemplate<ChallengesResDto>> getChallenges(
            @RequestParam(name = "type", required = false) ChallengeType type
    ) {
        return RspTemplate.<ChallengesResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("난이도별 챌린지 목록 조회")
                .data(challengeService.findChallengesByChallengeType(type))
                .build()
                .toResponseEntity();
    }

}
