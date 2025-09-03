package com.leafup.leafupbackend.image.api.dto.response;

import com.leafup.leafupbackend.image.domain.Image;
import lombok.Builder;

@Builder
public record ImageResDto(
        String convertImageUrl
) {
    public static ImageResDto from(Image image) {
        return ImageResDto.builder()
                .convertImageUrl(image.getConvertImageUrl())
                .build();
    }
}
