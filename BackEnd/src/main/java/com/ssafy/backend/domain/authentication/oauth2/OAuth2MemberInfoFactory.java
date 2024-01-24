package com.ssafy.backend.domain.authentication.oauth2;

import com.ssafy.backend.domain.authentication.exception.OAuthError;
import com.ssafy.backend.domain.authentication.exception.OAuthException;
import com.ssafy.backend.domain.member.exception.MemberError;
import com.ssafy.backend.domain.member.exception.MemberException;

import java.util.Map;

public class OAuth2MemberInfoFactory {

    public OAuth2MemberInfoFactory() {

    }

    public static OAuth2MemberInfo getOAuth2MemberInfo(String providerType, Map<String, Object> attributes) {
        return switch (providerType) {
            case "NAVER" -> new NaverOAuth2MemberInfo(attributes);
            case "KAKAO" -> new KakaoOAuth2MemberInfo(attributes);
            default -> throw new OAuthException(OAuthError.ABNORMAL_ACCESS);
        };
    }
}
