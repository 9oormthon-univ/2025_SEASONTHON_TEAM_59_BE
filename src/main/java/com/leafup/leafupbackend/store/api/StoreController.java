package com.leafup.leafupbackend.store.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.store.api.docs.StoreControllerDocs;
import com.leafup.leafupbackend.store.api.dto.response.StoreAvatarsResDto;
import com.leafup.leafupbackend.store.application.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController implements StoreControllerDocs {

    private final StoreService storeService;

    @GetMapping("/avatars")
    public ResponseEntity<RspTemplate<StoreAvatarsResDto>> getAvatarsForSale(@AuthenticatedEmail String email) {
        return RspTemplate.<StoreAvatarsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("상점 아바타 목록 조회 성공")
                .data(storeService.getAvatarsForSale(email))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/avatars/{avatarId}/purchase")
    public ResponseEntity<RspTemplate<Void>> purchaseAvatar(@AuthenticatedEmail String email, @PathVariable Long avatarId) {
        storeService.purchaseAvatar(email, avatarId);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("아바타 구매 성공")
                .build()
                .toResponseEntity();
    }

}
