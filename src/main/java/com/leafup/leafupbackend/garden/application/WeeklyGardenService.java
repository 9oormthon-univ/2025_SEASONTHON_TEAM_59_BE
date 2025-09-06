package com.leafup.leafupbackend.garden.application;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.challenge.domain.repository.ChallengeRepository;
import com.leafup.leafupbackend.garden.api.dto.response.CompletedChallengeDto;
import com.leafup.leafupbackend.garden.api.dto.response.WeeklyGardenResDto;
import com.leafup.leafupbackend.garden.domain.WeeklyGarden;
import com.leafup.leafupbackend.garden.domain.repository.WeeklyGardenRepository;
import com.leafup.leafupbackend.member.application.LevelService;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeeklyGardenService {

    private final WeeklyGardenRepository weeklyGardenRepository;
    private final MemberRepository memberRepository;
    private final ChallengeRepository challengeRepository;
    private final LevelService levelService;

    @Transactional
    public void recordChallengeCompletion(Member member, Challenge challenge) {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int weekOfYear = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        WeeklyGarden weeklyGarden = weeklyGardenRepository.findByMemberAndYearAndWeekOfYear(member, year, weekOfYear)
                .orElseGet(() -> {
                    WeeklyGarden newGarden = WeeklyGarden.builder()
                            .member(member)
                            .year(year)
                            .weekOfYear(weekOfYear)
                            .build();

                    return weeklyGardenRepository.save(newGarden);
                });

        boolean isNewChallenge = weeklyGarden.addCompletedChallenge(challenge.getId());

        if (isNewChallenge &&
                weeklyGarden.getCompletedChallengeIds().size() == 9 &&
                !weeklyGarden.isBonusAwarded()) {
            levelService.addPointAndHandleLevelUpAndExp(member, 100, "주간 텃밭 9칸 완성 보너스");
            weeklyGarden.markBonusAsAwarded();
        }
    }

    public WeeklyGardenResDto getWeeklyGardenStatus(String email) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int weekOfYear = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        Optional<WeeklyGarden> weeklyGardenOpt = weeklyGardenRepository.findByMemberAndYearAndWeekOfYear(member, year,
                weekOfYear);

        if (weeklyGardenOpt.isEmpty()) {
            return WeeklyGardenResDto.of(year, weekOfYear, Collections.emptyList());
        }

        WeeklyGarden weeklyGarden = weeklyGardenOpt.get();
        List<CompletedChallengeDto> completedChallenges = weeklyGarden.getCompletedChallengeIds().stream()
                .map(challengeId -> challengeRepository.findById(challengeId)
                        .map(c -> CompletedChallengeDto.of(c.getId(), c.getContents()))
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return WeeklyGardenResDto.of(year, weekOfYear, completedChallenges);
    }

}
