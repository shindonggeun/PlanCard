package com.ssafy.backend.domain.authentication.service;

import com.ssafy.backend.domain.authentication.dto.OAuthLoginResponse;
import com.ssafy.backend.domain.authentication.dto.OAuthSignupRequestDto;
import com.ssafy.backend.domain.authentication.dto.OAuthTokenResponse;
import com.ssafy.backend.domain.authentication.exception.OAuthError;
import com.ssafy.backend.domain.authentication.exception.OAuthException;
import com.ssafy.backend.domain.authentication.oauth2.OAuth2MemberInfo;
import com.ssafy.backend.domain.authentication.oauth2.OAuth2MemberInfoFactory;
import com.ssafy.backend.domain.member.dto.MemberLoginResponseDto;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.repository.MemberRepository;
import com.ssafy.backend.global.component.jwt.dto.TokenDto;
import com.ssafy.backend.global.component.jwt.dto.TokenMemberInfoDto;
import com.ssafy.backend.global.component.jwt.repository.RefreshRepository;
import com.ssafy.backend.global.component.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuthServiceImpl implements OAuthService {

    private final MemberRepository memberRepository;
    private final InMemoryClientRegistrationRepository inMemoryClientRegistrationRepository;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;

    @Override
    public MemberLoginResponseDto loginOauth(String code, String providerType) {
        ClientRegistration provider = inMemoryClientRegistrationRepository.findByRegistrationId(providerType);
        OAuthTokenResponse oAuthToken = getOAuth2Token(code, provider);
        Map<String, Object> attributes = getAttributes(oAuthToken, provider);
        OAuth2MemberInfo oAuth2MemberInfo = OAuth2MemberInfoFactory
                .getOAuth2MemberInfo(providerType.toUpperCase(), attributes);
        Optional<String> optionalEmail = oAuth2MemberInfo.getEmail();

        if (optionalEmail.isEmpty()) {
            throw new OAuthException(OAuthError.NOT_FOUND_EMAIL);
        }

        String email = optionalEmail.get();

        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if (optionalMember.isEmpty()) {
            String signUpToken = jwtService.issueSignUpToken(email, providerType.toUpperCase());
        }

        Member member = optionalMember.get();

        if (!member.getProviderType().toString().equals(providerType.toUpperCase())) {
            throw new OAuthException(OAuthError.MISS_MATCH_PROVIDER);
        }


        TokenDto tokenDto = getTokens(member);
        return null;
    }

    @Override
    public void signupOauth(OAuthSignupRequestDto signupRequestDto) {
        
    }

    private TokenDto getTokens(Member member) {
        return jwtService.issueToken(
                TokenMemberInfoDto.builder()
                        .id(member.getId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .nickname(member.getNickname())
                        .role(member.getRole().toString())
                        .build());
    }

    private OAuthTokenResponse getOAuth2Token(String code, ClientRegistration provider) {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(header -> {
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setBasicAuth(provider.getClientId(), provider.getClientSecret());
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        LinkedMultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", provider.getRedirectUri());
        formData.add("client_id", provider.getClientId());
        formData.add("client_secret", provider.getClientSecret());
        return formData;
    }

    private Map<String, Object> getAttributes(OAuthTokenResponse oAuthToken,
                                              ClientRegistration provider) {
        return WebClient.create()
                .get()
                .uri(provider.getProviderDetails().getUserInfoEndpoint().getUri())
                .headers(httpHeader -> httpHeader.setBearerAuth(oAuthToken.getAccessToken()))
                .retrieve().bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
