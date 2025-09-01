package com.leafup.leafupbackend.auth.application;

import com.leafup.leafupbackend.auth.api.dto.response.UserInfo;

public interface AuthService {

    UserInfo getUserInfo(String code);

    String getProvider();

    String getAuthUrl();
}
