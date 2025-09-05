package com.leafup.leafupbackend.admin.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.admin.api.docs.AdminControllerDocs;
import com.leafup.leafupbackend.admin.application.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController implements AdminControllerDocs {

    private final AdminService adminService;

    // 챌린지 승인
    @PostMapping("/{dailyMemberChallengeId}/approve")
    public ResponseEntity<RspTemplate<Void>> approveChallenge(
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId) {
        adminService.approveChallenge(dailyMemberChallengeId);

        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("챌린지 승인 완료")
                .build()
                .toResponseEntity();
    }

    // 챌린지 반려
    @PostMapping("/{dailyMemberChallengeId}/reject")
    public ResponseEntity<RspTemplate<Void>> rejectChallenge(
            @PathVariable("dailyMemberChallengeId") Long dailyMemberChallengeId) {
        adminService.rejectChallenge(dailyMemberChallengeId);

        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("챌린지 반려 완료")
                .build()
                .toResponseEntity();
    }

}
