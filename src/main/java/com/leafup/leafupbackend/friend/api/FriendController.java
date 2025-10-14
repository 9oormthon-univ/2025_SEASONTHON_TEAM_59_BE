package com.leafup.leafupbackend.friend.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.friend.api.docs.FriendControllerDocs;
import com.leafup.leafupbackend.friend.api.dto.request.FriendReqDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendsResDto;
import com.leafup.leafupbackend.friend.application.FriendService;
import com.leafup.leafupbackend.friend.domain.FriendshipStatus;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/friends")
public class FriendController implements FriendControllerDocs {

    private final FriendService friendService;

    @PostMapping("/request")
    public ResponseEntity<RspTemplate<Void>> sendFriendRequest(@AuthenticatedEmail String email,
                                                               @Valid @RequestBody FriendReqDto friendReqDto) {
        friendService.sendFriendRequest(email, friendReqDto);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.CREATED)
                .message("친구 요청")
                .build()
                .toResponseEntity();
    }

    @GetMapping("/requests")
    public ResponseEntity<RspTemplate<FriendsResDto>> getFriendRequests(@AuthenticatedEmail String email) {
        return RspTemplate.<FriendsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("받은 친구 요청 목록 조회")
                .data(friendService.getFriendRequests(email))
                .build()
                .toResponseEntity();
    }

    @GetMapping
    public ResponseEntity<RspTemplate<FriendsResDto>> getFriends(@AuthenticatedEmail String email) {
        return RspTemplate.<FriendsResDto>builder()
                .statusCode(HttpStatus.OK)
                .message("친구 목록 조회")
                .data(friendService.getFriends(email))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/requests/{friendshipId}/accept")
    public ResponseEntity<RspTemplate<Void>> acceptFriendRequest(@AuthenticatedEmail String email,
                                                                 @PathVariable(name = "friendshipId") Long friendshipId) {
        friendService.respondToFriendRequest(email, friendshipId, FriendshipStatus.ACCEPTED);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("친구 요청 수락")
                .build()
                .toResponseEntity();
    }

    @PostMapping("/requests/{friendshipId}/reject")
    public ResponseEntity<RspTemplate<Void>> rejectFriendRequest(@AuthenticatedEmail String email,
                                                                 @PathVariable(name = "friendshipId") Long friendshipId) {
        friendService.respondToFriendRequest(email, friendshipId, FriendshipStatus.REJECTED);
        return RspTemplate.<Void>builder()
                .statusCode(HttpStatus.OK)
                .message("친구 요청 거절")
                .build()
                .toResponseEntity();
    }

}
