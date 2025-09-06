package com.leafup.leafupbackend.admin.application;

import com.leafup.leafupbackend.admin.api.dto.response.PendingChallengeResDto;
import com.leafup.leafupbackend.admin.api.dto.response.PendingChallengesResDto;
import com.leafup.leafupbackend.garden.application.WeeklyGardenService;
import com.leafup.leafupbackend.member.application.LevelService;
import com.leafup.leafupbackend.member.domain.ChallengeStatus;
import com.leafup.leafupbackend.member.domain.DailyMemberChallenge;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeImageRepository;
import com.leafup.leafupbackend.member.domain.repository.DailyMemberChallengeRepository;
import com.leafup.leafupbackend.member.exception.DailyMemberChallengeNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final DailyMemberChallengeRepository dailyMemberChallengeRepository;
    private final DailyMemberChallengeImageRepository dailyMemberChallengeImageRepository;
    private final LevelService levelService;
    private final WeeklyGardenService weeklyGardenService;

    public PendingChallengesResDto getPendingChallenges() {
        List<PendingChallengeResDto> pendingChallenges = dailyMemberChallengeImageRepository
                .findByDailyMemberChallenge_ChallengeStatus(ChallengeStatus.PENDING_APPROVAL).stream()
                .map(image -> {
                    DailyMemberChallenge dmc = image.getDailyMemberChallenge();
                    return PendingChallengeResDto.of(
                            dmc.getId(),
                            dmc.getMember().getNickname(),
                            dmc.getChallenge().getContents(),
                            image.getImageUrl()
                    );
                })
                .collect(Collectors.toList());

        return PendingChallengesResDto.of(pendingChallenges);
    }

    @Transactional
    public void approveChallenge(Long dailyMemberChallengeId) {
        DailyMemberChallenge dmc = findDailyChallengeById(dailyMemberChallengeId);
        dmc.updateChallengeStatus(ChallengeStatus.COMPLETED);

        Member member = dmc.getMember();
        LocalDate challengeDate = dmc.getChallengeDate();

        levelService.addPointAndHandleLevelUpAndExp(member,
                dmc.getChallenge().getChallengeType().getPoint(),
                "데일리 챌린지 승인");

        weeklyGardenService.recordChallengeCompletion(member, dmc.getChallenge());

        int completedCount = dailyMemberChallengeRepository
                .countByMemberAndChallengeDateAndChallengeStatus(member, challengeDate, ChallengeStatus.COMPLETED);

        if (completedCount == 3) {
            levelService.addPointAndHandleLevelUpAndExp(member, 20, "일일 챌린지 3개 완료 보너스");
        }
    }

    @Transactional
    public void rejectChallenge(Long dailyMemberChallengeId) {
        DailyMemberChallenge dmc = findDailyChallengeById(dailyMemberChallengeId);
        dmc.updateChallengeStatus(ChallengeStatus.REJECTED);

        levelService.subtractPointAndHandleLevelAndExpDown(dmc.getMember(), dmc.getChallenge().getChallengeType().getPoint(),
                "챌린지 반려");
    }

    private DailyMemberChallenge findDailyChallengeById(Long id) {
        return dailyMemberChallengeRepository.findById(id).orElseThrow(DailyMemberChallengeNotFoundException::new);
    }

}
