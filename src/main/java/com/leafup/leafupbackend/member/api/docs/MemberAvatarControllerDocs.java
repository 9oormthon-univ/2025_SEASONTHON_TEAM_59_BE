package com.leafup.leafupbackend.member.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.member.api.dto.response.OwnedAvatarsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface MemberAvatarControllerDocs {

    @Operation(summary = "보유 아바타 목록 조회", description = "보유 아바타 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "보유 아바타 조회 성공")
    })
    ResponseEntity<RspTemplate<OwnedAvatarsResDto>> getOwnedAvatars(@AuthenticatedEmail String email);

    @Operation(summary = "아바타 장착", description = "아바타를 장착합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아바타 장착 성공")
    })
    ResponseEntity<RspTemplate<Void>> equipAvatar(@AuthenticatedEmail String email, @PathVariable("avatarId") Long avatarId);

}
