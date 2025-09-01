package com.leafup.leafupbackend.global.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "oauth")
public class OauthProperties {

    private final GoogleProperties google;
    private final KakaoProperties kakao;

    public record GoogleProperties(
            String clientId,
            String clientSecret,
            String redirectUri,
            String idTokenUrl
    ) {
    }

    public record KakaoProperties(
            String clientId,
            String redirectUri,
            String idTokenUrl
    ) {
    }

}