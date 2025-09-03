package com.leafup.leafupbackend.image.application;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.leafup.leafupbackend.image.domain.Image;
import com.leafup.leafupbackend.image.domain.repository.ImageRepository;
import com.leafup.leafupbackend.member.domain.Member;
import com.leafup.leafupbackend.member.domain.repository.MemberRepository;
import com.leafup.leafupbackend.member.exception.MemberNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String keyFileName;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String imageUpload(String email, MultipartFile multipartFile) throws IOException {
        String uuid = getUuid();
        Storage storage = getStorage();
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String filePath = "leafup/member/images/" + member.getId() + "/" + uuid;
        String imgUrl = getImgUrl(filePath);

        storageSave(multipartFile, filePath, storage);
        Image image = Image.builder()
                .convertImageUrl(imgUrl)
                .member(member)
                .build();
        imageRepository.save(image);

        return image.getConvertImageUrl();
    }

    @Transactional
    public String memberProfileImageUpload(String email, MultipartFile multipartFile) throws IOException {
        String uuid = getUuid();
        Storage storage = getStorage();

        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String filePath = "leafup/member/profile/" + member.getId() + "/" + uuid;
        String imgUrl = getImgUrl(filePath);

        if (multipartFile != null) {
            storageSave(multipartFile, filePath, storage);
            Image image = Image.builder()
                    .convertImageUrl(imgUrl)
                    .member(member)
                    .build();

            imageRepository.save(image);
            member.updatePicture(image.getConvertImageUrl());
        }

        return member.getPicture();
    }

    private static String getUuid() {
        return UUID.randomUUID().toString();
    }

    private Storage getStorage() throws IOException {
        InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();

        return StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();
    }

    private String getImgUrl(String filePath) {
        return "https://storage.googleapis.com/" + bucketName + "/" + filePath;
    }

    private void storageSave(MultipartFile file, String filePath, Storage storage) throws IOException {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, filePath)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getInputStream());
    }

}
