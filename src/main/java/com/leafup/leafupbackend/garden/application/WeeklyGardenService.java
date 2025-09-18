package com.leafup.leafupbackend.garden.application;

import com.leafup.leafupbackend.challenge.domain.Challenge;
import com.leafup.leafupbackend.garden.api.dto.response.GardenFruitDto;
import com.leafup.leafupbackend.garden.api.dto.response.WeeklyGardenResDto;
import com.leafup.leafupbackend.garden.domain.GardenFruit;
import com.leafup.leafupbackend.garden.domain.WeeklyGarden;
import com.leafup.leafupbackend.garden.domain.repository.WeeklyGardenRepository;
import com.leafup.leafupbackend.garden.excepion.FruitAlreadyHarvestedException;
import com.leafup.leafupbackend.garden.excepion.NoFruitToHarvestException;
import com.leafup.leafupbackend.member.application.LevelService;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

        boolean fruitExists = weeklyGarden.getFruits().stream()
                .anyMatch(fruit -> fruit.getChallenge().getId().equals(challenge.getId()));

        if (fruitExists) {
            return;
        }

        GardenFruit newFruit = GardenFruit.builder()
                .weeklyGarden(weeklyGarden)
                .challenge(challenge)
                .build();
        weeklyGarden.addFruit(newFruit);

        if (weeklyGarden.getFruits().size() == 9 && !weeklyGarden.isBonusAwarded()) {
            member.incrementWeeklyGardenCompletionCount();
            weeklyGarden.markBonusAsAwarded();
        }
    }

    @Transactional
    public void harvestFromGarden(String email, Long challengeId) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int weekOfYear = today.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());

        WeeklyGarden weeklyGarden = weeklyGardenRepository.findByMemberAndYearAndWeekOfYear(member, year, weekOfYear)
                .orElseThrow(NoFruitToHarvestException::new);

        GardenFruit fruitToHarvest = weeklyGarden.getFruits().stream()
                .filter(fruit -> fruit.getChallenge().getId().equals(challengeId))
                .findFirst()
                .orElseThrow(NoFruitToHarvestException::new);

        if (fruitToHarvest.isHarvested()) {
            throw new FruitAlreadyHarvestedException();
        }

        levelService.grantExpAndPoint(member, 9, "텃밭 열매 수확");
        fruitToHarvest.harvest();
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
        List<GardenFruitDto> fruits = weeklyGarden.getFruits().stream()
                .map(gardenFruit -> GardenFruitDto.from(
                        gardenFruit.getChallenge().getId(),
                        gardenFruit.getChallenge().getContents(),
                        gardenFruit.isHarvested()
                ))
                .toList();

        return WeeklyGardenResDto.of(year, weekOfYear, fruits);
    }

}
