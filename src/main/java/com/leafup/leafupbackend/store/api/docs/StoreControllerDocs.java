package com.leafup.leafupbackend.store.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.store.api.dto.response.StoreAvatarsResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface StoreControllerDocs {

    @Operation(summary = "상점 아바타 목록 조회", description = "상점 아바타 목록 조회 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 아바타 목록 조회 성공")
    })
    ResponseEntity<RspTemplate<StoreAvatarsResDto>> getAvatarsForSale(@AuthenticatedEmail String email);

    @Operation(summary = "아바타 구매", description = "아바타 구매 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "아바타 구매 성공")
    })
    ResponseEntity<RspTemplate<Void>> purchaseAvatar(@AuthenticatedEmail String email, @PathVariable Long avatarId);

}
