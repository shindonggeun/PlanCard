package com.ssafy.backend.domain.authentication.oauth2;

import java.util.Map;
import java.util.Optional;

public class NaverOAuth2MemberInfo extends OAuth2MemberInfo {
    public NaverOAuth2MemberInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("id");
    }

    @Override
    public String getName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return (String) response.get("name");
    }

    public Optional<String> getEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return Optional.ofNullable((String) response.get("email"));
    }
}
