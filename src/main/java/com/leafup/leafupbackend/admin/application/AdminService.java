package com.leafup.leafupbackend.admin.application;

import com.leafup.leafupbackend.member.application.LevelService;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.exception.DailyMemberChallengeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final LevelService levelService;

    // (관리자) 챌린지 승인 -> 추후 승인시 포인트 증가로 변경될 가능성 있음.
    @Transactional
    public void approveChallenge(Long dailyMemberChallengeId) {
        DailyMemberChallenge dmc = findDailyChallengeById(dailyMemberChallengeId);
        dmc.updateChallengeStatus(ChallengeStatus.COMPLETED);
    }

    // (관리자) 챌린지 반려
    @Transactional
    public void rejectChallenge(Long dailyMemberChallengeId) {
        DailyMemberChallenge dmc = findDailyChallengeById(dailyMemberChallengeId);
        dmc.updateChallengeStatus(ChallengeStatus.REJECTED);

        levelService.subtractPointAndHandleLevelAndExpDown(dmc.getMember(), dmc.getChallenge().getChallengeType().getPoint(), "챌린지 반려");
    }

    private DailyMemberChallenge findDailyChallengeById(Long id) {
        return dailyMemberChallengeRepository.findById(id).orElseThrow(DailyMemberChallengeNotFoundException::new);
    }

}
