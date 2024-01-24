package com.ssafy.backend.domain.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthLoginResponse {
    private Long id;
    private String email;
    private String name;
    private String nickname;
    private String role;
    private String signupToken;
    private String accessToken;
}
