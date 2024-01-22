package com.ssafy.backend.domain.authentication.service;

import com.ssafy.backend.domain.member.dto.MemberLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface OAuthService {
    MemberLoginResponseDto loginOauth(String code, String providerType);


}
