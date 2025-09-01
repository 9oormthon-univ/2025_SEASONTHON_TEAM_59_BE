package com.leafup.leafupbackend.auth.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceFactory {

    private final Map<String, AuthService> authServices;

    @Autowired
    public AuthServiceFactory(List<AuthService> services) {
        authServices = new HashMap<>();
        for (AuthService authService : services) {
            authServices.put(authService.getProvider(), authService);
        }
    }

    public AuthService getAuthService(String provider) {
        return authServices.get(provider);
    }

}
