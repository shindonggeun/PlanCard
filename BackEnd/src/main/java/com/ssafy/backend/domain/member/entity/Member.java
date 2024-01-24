package com.ssafy.backend.domain.member.entity;

import com.ssafy.backend.domain.member.entity.enums.MemberRole;
import com.ssafy.backend.domain.member.entity.enums.ProviderType;
import com.ssafy.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String nickname;

    private String image;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    // 회원 상태 나타내는 코드는 나중에

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateImage(String image) { this.image = image; }

}
