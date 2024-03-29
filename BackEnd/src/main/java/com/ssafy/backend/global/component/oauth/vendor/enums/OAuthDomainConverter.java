package com.ssafy.backend.global.component.oauth.vendor.enums;

import org.springframework.core.convert.converter.Converter;

public class OAuthDomainConverter implements Converter<String, OAuthDomain> {
    @Override
    public OAuthDomain convert(String source) {
        return OAuthDomain.fromName(source);
    }
}
