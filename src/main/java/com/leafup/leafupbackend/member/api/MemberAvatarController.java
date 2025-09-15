package com.leafup.leafupbackend.member.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.docs.MemberAvatarControllerDocs;
import com.leafup.leafupbackend.member.api.dto.response.OwnedAvatarsResDto;
import com.leafup.leafupbackend.member.application.MemberAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members/me/avatars")
public class MemberAvatarController implements MemberAvatarControllerDocs {

    private final MemberAvatarService memberAvatarService;

    @GetMapping
    public ResponseEntity<RspTemplate<OwnedAvatarsResDto>> getOwnedAvatars(@AuthenticatedEmail String email) {
        return RspTemplate.<OwnedAvatarsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("보유 아바타 목록 조회 성공")
                .data(memberAvatarService.getOwnedAvatars(email))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/{avatarId}/equip")
    public ResponseEntity<RspTemplate<Void>> equipAvatar(@AuthenticatedEmail String email, @PathVariable("avatarId") Long avatarId) {
        memberAvatarService.equipAvatar(email, avatarId);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("아바타 장착 성공")
                .build()
                .toResponseEntity();
    }

}
