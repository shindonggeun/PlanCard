package com.ssafy.backend.domain.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthLoginResponse {
    private Long id;
    private String nickname;
    private String image;
    private String signUpToken;
    private String accessToken;
    private boolean loginResult;
}
