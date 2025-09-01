package com.leafup.leafupbackend.global.oauth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leafup.leafupbackend.auth.api.dto.response.UserInfo;
import com.leafup.leafupbackend.auth.application.AuthService;
import com.leafup.leafupbackend.global.oauth.api.dto.response.GoogleTokenResponse;
import com.leafup.leafupbackend.global.oauth.exception.OAuthException;
import com.leafup.leafupbackend.global.properties.OauthProperties;
import com.leafup.leafupbackend.member.domain.SocialType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoogleAuthService implements AuthService {

    private static final String JWT_DELIMITER = "\\.";

    private final OauthProperties oauthProperties;

    private final ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getProvider() {
        return String.valueOf(SocialType.GOOGLE).toLowerCase();
    }

    @Override
    public String getAuthUrl() {
        return "https://accounts.google.com/o/oauth2/v2/auth?" +
                "client_id=" + oauthProperties.getGoogle().clientId() +
                "&redirect_uri=" + oauthProperties.getGoogle().redirectUri() +
                "&response_type=code" +
                "&scope=email%20profile%20openid";
    }

    @Override
    public UserInfo getUserInfo(String code) {
        String idToken = getIdToken(code);
        String decodePayload = getDecodePayload(idToken);

        try {
            return objectMapper.readValue(decodePayload, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new OAuthException("id 토큰을 읽을 수 없습니다.");
        }
    }

    private String getIdToken(String code) {
        HttpEntity<MultiValueMap<String, String>> requestEntity = createRequestEntity(code);

        try {
            ResponseEntity<GoogleTokenResponse> responseTokenEntity = restTemplate.postForEntity(
                    oauthProperties.getGoogle().idTokenUrl(),
                    requestEntity,
                    GoogleTokenResponse.class);

            return Objects.requireNonNull(responseTokenEntity.getBody()).getIdToken();
        } catch (RestClientException e) {
            throw new OAuthException();
        }
    }

    private HttpEntity<MultiValueMap<String, String>> createRequestEntity(String code) {
        return new HttpEntity<>(createRequestParams(code), createHttpHeaders());
    }

    private MultiValueMap<String, String> createRequestParams(String code) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

        requestParams.add("code", code);
        requestParams.add("client_id", oauthProperties.getGoogle().clientId());
        requestParams.add("client_secret", oauthProperties.getGoogle().clientSecret());
        requestParams.add("redirect_uri", oauthProperties.getGoogle().redirectUri());
        requestParams.add("grant_type", "authorization_code");

        return requestParams;
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return httpHeaders;
    }

    private String getDecodePayload(String idToken) {
        String payload = getPayload(idToken);

        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }

    private String getPayload(String idToken) {
        return idToken.split(JWT_DELIMITER)[1];
    }

}
