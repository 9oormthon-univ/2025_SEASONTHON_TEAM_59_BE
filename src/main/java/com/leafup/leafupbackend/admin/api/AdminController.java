package com.leafup.leafupbackend.admin.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.admin.api.docs.AdminControllerDocs;
import com.leafup.leafupbackend.admin.api.dto.response.PendingChallengesResDto;
import com.leafup.leafupbackend.admin.application.AdminService;
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
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    @Override
    @GetMapping("/challenges/pending")
    public ResponseEntity<RspTemplate<PendingChallengesResDto>> getPendingChallenges() {
        return RspTemplate.<PendingChallengesResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("승인 대기 중인 챌린지 목록 조회 성공")
                .data(adminService.getPendingChallenges())
                .build()
                .toResponseEntity();
    }

    @Override
    @PostMapping("/challenges/{dailyMemberChallengeId}/approve")
    public ResponseEntity<RspTemplate<Void>> approveChallenge(@PathVariable Long dailyMemberChallengeId) {
        adminService.approveChallenge(dailyMemberChallengeId);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("챌린지 승인 성공")
                .build()
                .toResponseEntity();
    }

    @Override
    @PostMapping("/challenges/{dailyMemberChallengeId}/reject")
    public ResponseEntity<RspTemplate<Void>> rejectChallenge(@PathVariable Long dailyMemberChallengeId) {
        adminService.rejectChallenge(dailyMemberChallengeId);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("챌린지 반려 성공")
                .build()
                .toResponseEntity();
    }
}
