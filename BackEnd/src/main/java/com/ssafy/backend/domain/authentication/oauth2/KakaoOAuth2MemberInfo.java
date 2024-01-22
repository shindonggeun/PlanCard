package com.ssafy.backend.domain.authentication.oauth2;

import java.util.Map;
import java.util.Optional;

public class KakaoOAuth2MemberInfo extends OAuth2MemberInfo {
    public KakaoOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        return (String) properties.get("nickname");
    }

    @Override
    public Optional<String> getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return Optional.ofNullable((String) kakaoAccount.get("email"));
    }
}
