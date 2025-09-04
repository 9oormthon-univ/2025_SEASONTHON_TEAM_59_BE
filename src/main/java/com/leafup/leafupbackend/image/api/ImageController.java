package com.leafup.leafupbackend.image.api;

import com.github.giwoong01.springapicommon.template.RspTemplate;
import com.leafup.leafupbackend.global.annotation.AuthenticatedEmail;
import com.leafup.leafupbackend.image.api.docs.ImageControllerDocs;
import com.leafup.leafupbackend.image.application.ImageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/images")
public class ImageController implements ImageControllerDocs {

    private final ImageService imageService;

    @PostMapping("/challenge/upload")
    public ResponseEntity<RspTemplate<String>> imageChallengeUpload(@AuthenticatedEmail String email,
                                                                    @RequestPart("multipartFile") MultipartFile multipartFile)
            throws IOException {
        return RspTemplate.<String>builder()
                .statusCode(HttpStatus.OK)
                .data(imageService.imageUpload(email, multipartFile))
                .build()
                .toResponseEntity();
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<RspTemplate<String>> imageProfileUpload(@AuthenticatedEmail String email,
                                                                  @RequestPart("multipartFile") MultipartFile multipartFile)
            throws IOException {
        return RspTemplate.<String>builder()
                .statusCode(HttpStatus.OK)
                .message("프로필 이미지 업로드 성공")
                .data(imageService.memberProfileImageUpload(email, multipartFile))
                .build()
                .toResponseEntity();
    }

}
