package com.ssafy.backend.domain.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthTokenResponse {
    private String idToken;
    private String tokenType;
    private String accessToken;
}
