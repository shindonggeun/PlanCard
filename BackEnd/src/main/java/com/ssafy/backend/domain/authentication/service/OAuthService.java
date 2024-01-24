package com.ssafy.backend.domain.authentication.service;

import com.ssafy.backend.domain.authentication.dto.OAuthLoginResponse;
import com.ssafy.backend.domain.authentication.dto.OAuthSignupRequestDto;

public interface OAuthService {
    OAuthLoginResponse loginOauth(String code, String providerType);

    void signupOauth(OAuthSignupRequestDto signupRequestDto);
}
