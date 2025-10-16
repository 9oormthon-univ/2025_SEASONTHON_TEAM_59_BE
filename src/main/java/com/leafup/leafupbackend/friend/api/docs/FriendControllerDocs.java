package com.leafup.leafupbackend.friend.api.docs;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.friend.api.dto.request.FriendReqDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendsResDto;
import com.leafup.leafupbackend.friend.api.dto.response.FriendshipsResDto;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public interface FriendControllerDocs {

    @Operation(summary = "친구 검색", description = "친구를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 검색 성공")
    })
    ResponseEntity<RspTemplate<FriendResDto>> getSearchMember(@RequestParam("nicknameWithCode") String nicknameWithCode);

    @Operation(summary = "친구 요청 보내기", description = "친구 요청을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "친구 요청 보내기 성공")
    })
    ResponseEntity<RspTemplate<Void>> sendFriendRequest(@AuthenticatedEmail String email,
                                                        @Valid @RequestBody FriendReqDto friendReqDto);

    @Operation(summary = "받은 친구 요청 목록 조회", description = "받은 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "받은 친구 요청 목록 조회 성공")
    })
    ResponseEntity<RspTemplate<FriendshipsResDto>> getFriendRequests(@AuthenticatedEmail String email);

    @Operation(summary = "친구 목록 조회", description = "친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 목록 조회 성공")
    })
    ResponseEntity<RspTemplate<FriendsResDto>> getFriends(@AuthenticatedEmail String email);

    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 수락 성공")
    })
    ResponseEntity<RspTemplate<Void>> acceptFriendRequest(@AuthenticatedEmail String email,
                                                          @PathVariable(name = "friendshipId") Long friendshipId);

    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 요청 거절 성공")
    })
    ResponseEntity<RspTemplate<Void>> rejectFriendRequest(@AuthenticatedEmail String email,
                                                          @PathVariable(name = "friendshipId") Long friendshipId);

    @Operation(summary = "친구 정보 조회", description = "친구 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "친구 정보 조회 성공")
    })
    ResponseEntity<RspTemplate<FriendResDto>> getFriendDetails(@AuthenticatedEmail String email,
                                                               @PathVariable(name = "friendId") Long friendId);


}
