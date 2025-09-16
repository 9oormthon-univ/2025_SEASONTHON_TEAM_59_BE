package com.leafup.leafupbackend.achievement.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.achievement.api.dto.response.AchievementStatusResDto;
import com.leafup.leafupbackend.achievement.api.dto.response.ClaimedAchievementResDto;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface AchievementControllerDocs {

    @Operation(summary = "사용자별 업적 현황 조회", description = "사용자별 업적 현황을 조회합니다. 업적 상태 (LOCKED, UNLOCKED, CLAIMED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자별 업적 현황 조회 성공")
    })
    ResponseEntity<RspTemplate<List<AchievementStatusResDto>>> getAchievementStatus(
            @AuthenticatedEmail String email);

    @Operation(summary = "내가 획득한 업적 조회", description = "내가 획득한 업적을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 획득한 업적 조회 성공")
    })
    ResponseEntity<RspTemplate<List<ClaimedAchievementResDto>>> getMyClaimedAchievements(
            @AuthenticatedEmail String email);

    @Operation(summary = "업적 획득", description = "조건에 맞아 업적을 획득합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업적 획득 성공")
    })
    ResponseEntity<RspTemplate<Void>> claimAchievement(@AuthenticatedEmail String email,
                                                       @PathVariable Long achievementId);

}