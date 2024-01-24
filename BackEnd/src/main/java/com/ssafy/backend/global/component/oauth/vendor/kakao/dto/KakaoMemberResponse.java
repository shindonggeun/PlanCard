package com.ssafy.backend.global.component.oauth.vendor.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ssafy.backend.domain.member.entity.Member;
import com.ssafy.backend.domain.member.entity.enums.MemberRole;
import com.ssafy.backend.global.component.oauth.vendor.enums.OAuthDomain;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Kakao API로부터 받은 사용자 정보에 대한 응답을 나타내는 클래스.
 */
@Getter
@Setter
@JsonNaming(SnakeCaseStrategy.class)
public class KakaoMemberResponse {

    private Long id;
    private boolean hasSignedUp;
    private LocalDateTime connectedAt;
    private KakaoAccount kakaoAccount;

    /**
     * Member 도메인 객체로 변환합니다.
     *
     * @return 변환된 Member 객체.
     */
    public Member toDomain() {
        return Member.builder()
                .email(kakaoAccount.getEmail())
                .nickname(kakaoAccount.getProfile().getNickname())
                .image(kakaoAccount.getProfile().getImage())
                .role(MemberRole.USER)
                .oAuthDomain(OAuthDomain.KAKAO)
                .build();
    }

    /**
     * 카카오 계정 정보를 나타내는 클래스.
     */
    @Getter
    @Setter
    @JsonNaming(SnakeCaseStrategy.class)
    public static class KakaoAccount {
        private Profile profile;
        private String email;
    }

    /**
     * 카카오 프로필 정보를 나타내는 클래스.
     */
    @Getter
    @Setter
    @JsonNaming(SnakeCaseStrategy.class)
    public static class Profile {
        private String nickname;
        private String image;
    }
}

