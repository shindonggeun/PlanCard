package com.ssafy.backend.domain.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OAuthSignupRequestDto {
    private String signupToken;
    private String email;
    private String name;
    private String nickname;
}
