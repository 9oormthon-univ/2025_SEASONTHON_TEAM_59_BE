package com.leafup.leafupbackend.achievement.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.achievement.api.docs.AchievementControllerDocs;
import com.leafup.leafupbackend.achievement.api.dto.response.AchievementStatusResDto;
import com.leafup.leafupbackend.achievement.api.dto.response.ClaimedAchievementResDto;
import com.leafup.leafupbackend.achievement.application.AchievementService;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/achievements")
public class AchievementController implements AchievementControllerDocs {

    private final AchievementService achievementService;

    @GetMapping("/me")
    public ResponseEntity<RspTemplate<List<AchievementStatusResDto>>> getAchievementStatus(
            @AuthenticatedEmail String email) {
        List<AchievementStatusResDto> achievementStatus = achievementService.getAchievementStatus(email);
        return RspTemplate.<List<AchievementStatusResDto>>builder()
                .statusCode(HttpStatus.OK)
                .message("사용자별 업적 현황 조회 성공")
                .data(achievementStatus)
                .build()
                .toResponseEntity();
    }

    @GetMapping("/me/claimed")
    public ResponseEntity<RspTemplate<List<ClaimedAchievementResDto>>> getMyClaimedAchievements(
            @AuthenticatedEmail String email) {
        List<ClaimedAchievementResDto> claimedAchievements = achievementService.getClaimedAchievements(email);
        return RspTemplate.<List<ClaimedAchievementResDto>>builder()
                .statusCode(HttpStatus.OK)
                .message("획득한 업적 목록 조회 성공")
                .data(claimedAchievements)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/{achievementId}/claim")
    public ResponseEntity<RspTemplate<Void>> claimAchievement(@AuthenticatedEmail String email,
                                                              @PathVariable Long achievementId) {
        achievementService.claimAchievement(email, achievementId);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("업적 획득 성공")
                .build()
                .toResponseEntity();
    }

}