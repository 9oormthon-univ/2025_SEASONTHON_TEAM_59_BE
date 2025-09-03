package com.leafup.leafupbackend.image.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface ImageControllerDocs {


    @Operation(summary = "이미지 업로드", description = "이미지 업로드 합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증실패", content = @Content(schema = @Schema(example = "INVALID_HEADER or INVALID_TOKEN"))),
    })
    ResponseEntity<RspTemplate<String>> imageUpload(@AuthenticatedEmail String email,
                                                    @RequestPart("multipartFile") MultipartFile multipartFile)
            throws IOException;

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지 업로드 합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "401", description = "인증실패", content = @Content(schema = @Schema(example = "INVALID_HEADER or INVALID_TOKEN"))),
    })
    ResponseEntity<RspTemplate<String>> imageProfileUpload(@AuthenticatedEmail String email,
                                                           @RequestPart("multipartFile") MultipartFile multipartFile)
            throws IOException;
}
